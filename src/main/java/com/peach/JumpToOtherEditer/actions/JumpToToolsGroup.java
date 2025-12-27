package com.peach.JumpToOtherEditer.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.vfs.VirtualFile;
import com.peach.JumpToOtherEditer.model.EditorConfig;
import com.peach.JumpToOtherEditer.services.AntigravityLauncher;
import com.peach.JumpToOtherEditer.settings.AppSettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Dynamic action group that shows enabled editors in the context menu.
 * Editors are dynamically loaded from settings, allowing users to add custom
 * editors.
 */
public class JumpToToolsGroup extends ActionGroup implements DumbAware {

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        AppSettingsState settings = AppSettingsState.getInstance();
        List<EditorConfig> enabledEditors = settings.getEnabledEditors();

        AnAction[] actions = new AnAction[enabledEditors.size()];
        for (int i = 0; i < enabledEditors.size(); i++) {
            EditorConfig editor = enabledEditors.get(i);
            actions[i] = new OpenInEditorAction(editor);
        }

        return actions;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Show the group only if there are enabled editors
        AppSettingsState settings = AppSettingsState.getInstance();
        List<EditorConfig> enabledEditors = settings.getEnabledEditors();
        e.getPresentation().setEnabledAndVisible(!enabledEditors.isEmpty());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * Action to open a file in a specific editor.
     * This action is dynamically created based on EditorConfig.
     */
    private static class OpenInEditorAction extends AnAction implements DumbAware {
        private final EditorConfig editorConfig;

        public OpenInEditorAction(EditorConfig editorConfig) {
            super("Open in " + editorConfig.name);
            this.editorConfig = editorConfig;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
            if (virtualFile == null)
                return;

            String filePath = virtualFile.getPath();

            Editor editor = e.getData(CommonDataKeys.EDITOR);
            Integer lineNumber = null;
            if (editor != null) {
                // logicalPosition is 0-indexed, so we add 1
                lineNumber = editor.getCaretModel().getLogicalPosition().line + 1;
            }

            AntigravityLauncher.open(
                    editorConfig.path,
                    editorConfig.command,
                    editorConfig.name,
                    filePath,
                    lineNumber,
                    null,
                    true);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            // Always enable, let the action handle missing file gracefully
            e.getPresentation().setEnabledAndVisible(true);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }
}
