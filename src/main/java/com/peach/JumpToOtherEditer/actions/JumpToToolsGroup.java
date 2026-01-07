package com.peach.JumpToOtherEditer.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.peach.JumpToOtherEditer.model.EditorConfig;
import com.peach.JumpToOtherEditer.services.AntigravityLauncher;
import com.peach.JumpToOtherEditer.settings.AppSettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 动态 Action 组，在右键菜单中显示已启用的编辑器。
 * 编辑器从设置中动态加载，允许用户添加自定义编辑器。
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
        // 只有当存在已启用的编辑器时才显示该组
        AppSettingsState settings = AppSettingsState.getInstance();
        List<EditorConfig> enabledEditors = settings.getEnabledEditors();
        e.getPresentation().setEnabledAndVisible(!enabledEditors.isEmpty());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * 在指定编辑器中打开文件的 Action。
     * 此 Action 根据 EditorConfig 动态创建。
     */
    private static class OpenInEditorAction extends AnAction implements DumbAware {
        private final EditorConfig editorConfig;

        public OpenInEditorAction(EditorConfig editorConfig) {
            super("在 " + editorConfig.name + " 中打开");
            this.editorConfig = editorConfig;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
            if (virtualFile == null)
                return;

            String filePath = virtualFile.getPath();

            // 获取当前 IDEA 项目的根目录
            Project project = e.getProject();
            String projectRoot = null;
            if (project != null) {
                projectRoot = project.getBasePath();
            }

            Editor editor = e.getData(CommonDataKeys.EDITOR);
            Integer lineNumber = null;
            if (editor != null) {
                // logicalPosition 是 0 索引的，所以需要加 1
                lineNumber = editor.getCaretModel().getLogicalPosition().line + 1;
            }

            AntigravityLauncher.open(
                    editorConfig.path,
                    editorConfig.command,
                    editorConfig.name,
                    filePath,
                    lineNumber,
                    null,
                    projectRoot);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            // 始终启用，让 action 优雅地处理文件缺失的情况
            e.getPresentation().setEnabledAndVisible(true);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }
}
