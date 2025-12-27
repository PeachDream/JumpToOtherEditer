package com.peach.JumpToOtherEditer.services;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for launching external editors.
 * All supported editors are VS Code based and share the same CLI syntax:
 * <code>command -r -g &lt;filepath&gt;:&lt;line&gt;</code>
 */
public class AntigravityLauncher {
    private static final Logger logger = Logger.getInstance(AntigravityLauncher.class);
    private static final String NOTIFICATION_GROUP_ID = "JumpToOtherEditer";

    // Common installation paths for VS Code based editors on Windows
    private static final Map<String, String[]> WINDOWS_DEFAULT_PATHS = new HashMap<>();

    static {
        String userHome = System.getProperty("user.home");
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData == null) {
            localAppData = userHome + "\\AppData\\Local";
        }

        // VS Code
        WINDOWS_DEFAULT_PATHS.put("code", new String[] {
                localAppData + "\\Programs\\Microsoft VS Code\\bin\\code.cmd",
                localAppData + "\\Programs\\Microsoft VS Code\\Code.exe",
                "C:\\Program Files\\Microsoft VS Code\\bin\\code.cmd",
                "C:\\Program Files\\Microsoft VS Code\\Code.exe"
        });

        // Cursor
        WINDOWS_DEFAULT_PATHS.put("cursor", new String[] {
                localAppData + "\\Programs\\cursor\\resources\\app\\bin\\cursor.cmd",
                localAppData + "\\Programs\\cursor\\Cursor.exe",
                localAppData + "\\Cursor\\Cursor.exe"
        });

        // Antigravity
        WINDOWS_DEFAULT_PATHS.put("antigravity", new String[] {
                localAppData + "\\Programs\\Antigravity\\bin\\antigravity.cmd",
                localAppData + "\\Programs\\Antigravity\\Antigravity.exe",
                "C:\\Program Files\\Antigravity\\bin\\antigravity.cmd"
        });

        // Windsurf (Codeium)
        WINDOWS_DEFAULT_PATHS.put("windsurf", new String[] {
                localAppData + "\\Programs\\Windsurf\\bin\\windsurf.cmd",
                localAppData + "\\Programs\\Windsurf\\Windsurf.exe",
                "C:\\Program Files\\Windsurf\\bin\\windsurf.cmd"
        });

        // Trae
        WINDOWS_DEFAULT_PATHS.put("trae", new String[] {
                localAppData + "\\Programs\\Trae\\bin\\trae.cmd",
                localAppData + "\\Programs\\Trae\\Trae.exe"
        });

        // Trae CN (中国版)
        WINDOWS_DEFAULT_PATHS.put("trae-cn", new String[] {
                localAppData + "\\Programs\\Trae CN\\bin\\trae-cn.cmd",
                localAppData + "\\Programs\\Trae CN\\Trae CN.exe"
        });

        // CodeBuddy (Tencent)
        WINDOWS_DEFAULT_PATHS.put("buddy", new String[] {
                localAppData + "\\Programs\\CodeBuddy\\bin\\buddy.cmd",
                localAppData + "\\Programs\\CodeBuddy\\CodeBuddy.exe"
        });

        // Qoder
        WINDOWS_DEFAULT_PATHS.put("qoder", new String[] {
                localAppData + "\\Programs\\Qoder\\bin\\qoder.cmd",
                localAppData + "\\Programs\\Qoder\\Qoder.exe"
        });
    }

    /**
     * Opens a file in the specified external editor.
     *
     * @param executablePath  The configured absolute path to the executable
     *                        (optional)
     * @param toolCommand     The default command name (e.g., "code", "cursor") if
     *                        path is not configured
     * @param toolDisplayName The display name of the editor for notifications
     * @param filePath        The absolute path to the file
     * @param lineNumber      The line number to navigate to (1-indexed, optional)
     * @param columnNumber    The column number to navigate to (1-indexed, optional)
     * @param reuseWindow     Whether to reuse an existing window
     */
    public static void open(String executablePath, String toolCommand, String toolDisplayName,
            String filePath, @Nullable Integer lineNumber, @Nullable Integer columnNumber,
            boolean reuseWindow) {

        logger.info("=== AntigravityLauncher.open() ===");
        logger.info("Editor: " + toolDisplayName);
        logger.info("Command: " + toolCommand);
        logger.info("Path: " + (executablePath != null ? executablePath : "(auto-detect)"));
        logger.info("File: " + filePath);

        String finalCommand = determineExecutable(executablePath, toolCommand);
        logger.info("Resolved executable: " + finalCommand);

        List<String> command = buildCommand(finalCommand, filePath, lineNumber, columnNumber, reuseWindow);
        logger.info("Full command: " + String.join(" ", command));

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // Read process output in background
            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    StringBuilder output = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        logger.warn(toolDisplayName + " exited with code " + exitCode + ": " + output);
                    }
                } catch (Exception e) {
                    logger.error("Error reading process output", e);
                }
            }).start();

            String fileName = Paths.get(filePath).getFileName().toString();
            showNotification(
                    "Opened in " + toolDisplayName,
                    "File: " + fileName,
                    NotificationType.INFORMATION);

        } catch (IOException e) {
            String errorMessage = "Failed to launch " + toolDisplayName + ": " + e.getMessage();
            logger.error(errorMessage, e);

            showNotification(
                    "Failed to open " + toolDisplayName,
                    "Error: " + e.getMessage() + "\n\nPlease check if " + toolDisplayName +
                            " is installed and the command '" + toolCommand + "' is in PATH, " +
                            "or configure the executable path in Settings → Tools → JumpToOtherEditer.",
                    NotificationType.ERROR);
        }
    }

    /**
     * Determine the executable to use.
     * Priority: configured path > auto-detected path > command name
     */
    private static String determineExecutable(String configuredPath, String command) {
        // If a path is configured, use it
        if (configuredPath != null && !configuredPath.trim().isEmpty()) {
            return configuredPath.trim();
        }

        // Try to find in default Windows paths
        if (isWindows()) {
            String[] defaultPaths = WINDOWS_DEFAULT_PATHS.get(command.toLowerCase());
            if (defaultPaths != null) {
                for (String path : defaultPaths) {
                    File file = new File(path);
                    if (file.exists() && file.canExecute()) {
                        logger.info("Found " + command + " at: " + path);
                        return path;
                    }
                }
            }
        }

        // Fallback to command name (will use PATH)
        return command;
    }

    private static List<String> buildCommand(String executable, String filePath,
            @Nullable Integer lineNumber, @Nullable Integer columnNumber, boolean reuseWindow) {
        List<String> command = new ArrayList<>();

        // On Windows, use cmd.exe for proper PATH handling
        if (isWindows()) {
            command.add("cmd.exe");
            command.add("/c");
        }

        command.add(executable);

        // -r flag to reuse existing window
        if (reuseWindow) {
            command.add("-r");
        }

        // Find project root for folder tree view
        String projectRoot = findProjectRoot(filePath);
        if (projectRoot != null) {
            command.add(projectRoot);
        }

        // -g flag with file:line:column format
        command.add("-g");

        StringBuilder location = new StringBuilder(filePath);
        if (lineNumber != null) {
            location.append(":").append(lineNumber);
            if (columnNumber != null) {
                location.append(":").append(columnNumber);
            }
        }
        command.add(location.toString());

        return command;
    }

    /**
     * Find the project root directory by looking for common project markers.
     */
    @Nullable
    private static String findProjectRoot(String filePath) {
        File file = new File(filePath);
        File directory = file.isDirectory() ? file : file.getParentFile();

        String[] projectMarkers = {
                ".git", ".idea", ".vscode",
                "pom.xml", "build.gradle", "build.gradle.kts",
                "package.json", "Cargo.toml", "go.mod",
                "requirements.txt", "pyproject.toml",
                ".project", "CMakeLists.txt", "Makefile"
        };

        while (directory != null) {
            for (String marker : projectMarkers) {
                File markerFile = new File(directory, marker);
                if (markerFile.exists()) {
                    logger.info("Found project root: " + directory.getAbsolutePath());
                    return directory.getAbsolutePath();
                }
            }
            directory = directory.getParentFile();
        }

        // Fallback to parent directory
        File parentDir = new File(filePath).getParentFile();
        return parentDir != null ? parentDir.getAbsolutePath() : null;
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("win");
    }

    private static void showNotification(String title, String content, NotificationType type) {
        try {
            Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
            Project project = openProjects.length > 0 ? openProjects[0] : null;

            NotificationGroupManager.getInstance()
                    .getNotificationGroup(NOTIFICATION_GROUP_ID)
                    .createNotification(title, content, type)
                    .notify(project);
        } catch (Exception e) {
            logger.debug("Could not show notification: " + e.getMessage());
        }
    }
}
