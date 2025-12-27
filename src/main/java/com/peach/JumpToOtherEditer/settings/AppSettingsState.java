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

@State(name = "com.peach.jumptoantigravity.settings.AppSettingsState", storages = @Storage("JumpToOtherEditerSettings.xml"))
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    /**
     * List of editor configurations (dynamic)
     */
    @XCollection(propertyElementName = "editors", elementTypes = EditorConfig.class)
    public List<EditorConfig> editors = new ArrayList<>();

    /**
     * Flag to track if default editors have been initialized
     */
    public boolean defaultsInitialized = false;

    public AppSettingsState() {
        // Initialize with default editors on first use
        initializeDefaultEditors();
    }

    /**
     * Initialize default built-in editors
     */
    private void initializeDefaultEditors() {
        if (defaultsInitialized || !editors.isEmpty()) {
            return;
        }

        // Add built-in editors (VS Code and popular forks)
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
        // Ensure defaults are initialized even for existing installations
        if (!state.defaultsInitialized && state.editors.isEmpty()) {
            state.initializeDefaultEditors();
        }
        return state;
    }

    /**
     * Get list of enabled editors
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
     * Add a new custom editor
     */
    public EditorConfig addEditor(String name, String command) {
        EditorConfig editor = new EditorConfig(name, command, false);
        editors.add(editor);
        return editor;
    }

    /**
     * Remove an editor by ID (only non-builtin editors can be removed)
     */
    public boolean removeEditor(String id) {
        return editors.removeIf(e -> e.id.equals(id) && !e.builtin);
    }

    /**
     * Find an editor by ID
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
     * Deep copy the editors list
     */
    public List<EditorConfig> copyEditors() {
        List<EditorConfig> copy = new ArrayList<>();
        for (EditorConfig editor : editors) {
            copy.add(editor.copy());
        }
        return copy;
    }

    /**
     * Replace editors list with a copy
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
        // Ensure defaults exist after loading
        if (!defaultsInitialized && editors.isEmpty()) {
            initializeDefaultEditors();
        }
    }
}
