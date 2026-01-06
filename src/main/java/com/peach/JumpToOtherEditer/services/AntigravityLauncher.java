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
 * 负责启动外部编辑器的服务类。
 * 所有支持的编辑器都基于 VS Code，使用相同的命令行语法：
 * <code>command -g &lt;filepath&gt;:&lt;line&gt;</code>
 */
public class AntigravityLauncher {
    private static final Logger logger = Logger.getInstance(AntigravityLauncher.class);
    private static final String NOTIFICATION_GROUP_ID = "JumpToOtherEditer";

    // Windows 系统下 VS Code 系列编辑器的常见安装路径
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

        // Trae CN（中国版）
        WINDOWS_DEFAULT_PATHS.put("trae-cn", new String[] {
                localAppData + "\\Programs\\Trae CN\\bin\\trae-cn.cmd",
                localAppData + "\\Programs\\Trae CN\\Trae CN.exe"
        });

        // CodeBuddy（腾讯）
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
     * 在指定的外部编辑器中打开文件。
     *
     * @param executablePath  可执行文件的绝对路径（可选）
     * @param toolCommand     默认命令名称（如 "code", "cursor"），当未配置路径时使用
     * @param toolDisplayName 编辑器的显示名称，用于通知
     * @param filePath        文件的绝对路径
     * @param lineNumber      要跳转到的行号（1索引，可选）
     * @param columnNumber    要跳转到的列号（1索引，可选）
     * @param reuseWindow     （已弃用/忽略）现在由 VS Code 自动判断：
     *                        如果文件在当前工作区则复用窗口，否则打开新窗口
     */
    public static void open(String executablePath, String toolCommand, String toolDisplayName,
            String filePath, @Nullable Integer lineNumber, @Nullable Integer columnNumber,
            boolean reuseWindow) {

        logger.info("=== AntigravityLauncher.open() 开始 ===");
        logger.info("编辑器: " + toolDisplayName);
        logger.info("命令: " + toolCommand);
        logger.info("路径: " + (executablePath != null ? executablePath : "（自动检测）"));
        logger.info("文件: " + filePath);

        String finalCommand = determineExecutable(executablePath, toolCommand);
        logger.info("解析后的可执行文件: " + finalCommand);

        List<String> command = buildCommand(finalCommand, filePath, lineNumber, columnNumber, reuseWindow);
        logger.info("完整命令: " + String.join(" ", command));

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // 在后台线程中读取进程输出
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
                        logger.warn(toolDisplayName + " 退出码: " + exitCode + "，输出: " + output);
                    }
                } catch (Exception e) {
                    logger.error("读取进程输出时出错", e);
                }
            }).start();

            String fileName = Paths.get(filePath).getFileName().toString();
            showNotification(
                    "已在 " + toolDisplayName + " 中打开",
                    "文件: " + fileName,
                    NotificationType.INFORMATION);

        } catch (IOException e) {
            String errorMessage = "启动 " + toolDisplayName + " 失败: " + e.getMessage();
            logger.error(errorMessage, e);

            showNotification(
                    "打开 " + toolDisplayName + " 失败",
                    "错误: " + e.getMessage() + "\n\n请检查 " + toolDisplayName +
                            " 是否已安装，命令 '" + toolCommand + "' 是否在 PATH 中，" +
                            "或在 设置 → 工具 → JumpToOtherEditer 中配置可执行文件路径。",
                    NotificationType.ERROR);
        }
    }

    /**
     * 确定要使用的可执行文件。
     * 优先级：配置的路径 > 自动检测的路径 > 命令名称
     */
    private static String determineExecutable(String configuredPath, String command) {
        // 如果配置了路径，使用它
        if (configuredPath != null && !configuredPath.trim().isEmpty()) {
            return configuredPath.trim();
        }

        // 尝试在 Windows 默认路径中查找
        if (isWindows()) {
            String[] defaultPaths = WINDOWS_DEFAULT_PATHS.get(command.toLowerCase());
            if (defaultPaths != null) {
                for (String path : defaultPaths) {
                    File file = new File(path);
                    if (file.exists() && file.canExecute()) {
                        logger.info("找到 " + command + " 位于: " + path);
                        return path;
                    }
                }
            }
        }

        // 回退到命令名称（使用 PATH 环境变量）
        return command;
    }

    private static List<String> buildCommand(String executable, String filePath,
            @Nullable Integer lineNumber, @Nullable Integer columnNumber, boolean reuseWindow) {
        List<String> command = new ArrayList<>();

        // 在 Windows 上使用 cmd.exe 以正确处理 PATH
        if (isWindows()) {
            command.add("cmd.exe");
            command.add("/c");
        }

        command.add(executable);

        // 注意：这里不使用 -r 参数，让 VS Code 自动智能判断：
        // - 如果文件在已打开的工作区中，则复用该窗口
        // - 如果文件不在任何已打开的工作区中，则打开新窗口
        // 这为多项目工作流提供了最佳的用户体验

        // 查找项目根目录以显示文件夹树
        String projectRoot = findProjectRoot(filePath);
        if (projectRoot != null) {
            command.add(projectRoot);
        }

        // 使用 -g 参数指定 文件:行:列 格式
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
     * 通过查找常见的项目标记文件来查找项目根目录。
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
                    logger.info("找到项目根目录: " + directory.getAbsolutePath());
                    return directory.getAbsolutePath();
                }
            }
            directory = directory.getParentFile();
        }

        // 回退到父目录
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
            logger.debug("无法显示通知: " + e.getMessage());
        }
    }
}
