package com.peach.JumpToOtherEditer.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.XCollection;
import com.peach.JumpToOtherEditer.model.EditorConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置状态持久化服务。
 * 管理编辑器配置的保存和加载。
 */
@State(name = "com.peach.jumptoantigravity.settings.AppSettingsState", storages = @Storage("JumpToOtherEditerSettings.xml"))
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    /**
     * 编辑器配置列表（动态）
     */
    @XCollection(propertyElementName = "editors", elementTypes = EditorConfig.class)
    public List<EditorConfig> editors = new ArrayList<>();

    /**
     * 标记默认编辑器是否已初始化
     */
    public boolean defaultsInitialized = false;

    public AppSettingsState() {
        // 首次使用时初始化默认编辑器
        initializeDefaultEditors();
    }

    /**
     * 初始化默认的内置编辑器
     */
    private void initializeDefaultEditors() {
        if (defaultsInitialized || !editors.isEmpty()) {
            return;
        }

        // 添加内置编辑器（VS Code 及其常见分支）
        editors.add(new EditorConfig("VS Code", "code", true));
        editors.add(new EditorConfig("Cursor", "cursor", true));
        editors.add(new EditorConfig("Antigravity", "antigravity", true));
        editors.add(new EditorConfig("Windsurf", "windsurf", true));
        editors.add(new EditorConfig("Trae", "trae", true));
        editors.add(new EditorConfig("Trae CN", "trae-cn", true));
        editors.add(new EditorConfig("CodeBuddy", "buddy", true));

        defaultsInitialized = true;
    }

    public static AppSettingsState getInstance() {
        AppSettingsState state = ApplicationManager.getApplication().getService(AppSettingsState.class);
        // 确保即使对于已有安装也初始化默认值
        if (!state.defaultsInitialized && state.editors.isEmpty()) {
            state.initializeDefaultEditors();
        }
        return state;
    }

    /**
     * 获取已启用的编辑器列表
     */
    public List<EditorConfig> getEnabledEditors() {
        List<EditorConfig> enabled = new ArrayList<>();
        for (EditorConfig editor : editors) {
            if (editor.enabled) {
                enabled.add(editor);
            }
        }
        return enabled;
    }

    /**
     * 添加新的自定义编辑器
     */
    public EditorConfig addEditor(String name, String command) {
        EditorConfig editor = new EditorConfig(name, command, false);
        editors.add(editor);
        return editor;
    }

    /**
     * 通过 ID 删除编辑器（只能删除非内置编辑器）
     */
    public boolean removeEditor(String id) {
        return editors.removeIf(e -> e.id.equals(id) && !e.builtin);
    }

    /**
     * 通过 ID 查找编辑器
     */
    @Nullable
    public EditorConfig findEditorById(String id) {
        for (EditorConfig editor : editors) {
            if (editor.id.equals(id)) {
                return editor;
            }
        }
        return null;
    }

    /**
     * 深拷贝编辑器列表
     */
    public List<EditorConfig> copyEditors() {
        List<EditorConfig> copy = new ArrayList<>();
        for (EditorConfig editor : editors) {
            copy.add(editor.copy());
        }
        return copy;
    }

    /**
     * 用拷贝替换编辑器列表
     */
    public void setEditors(List<EditorConfig> newEditors) {
        editors.clear();
        for (EditorConfig editor : newEditors) {
            editors.add(editor.copy());
        }
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
        // 加载后确保默认值存在
        if (!defaultsInitialized && editors.isEmpty()) {
            initializeDefaultEditors();
        }
    }
}
