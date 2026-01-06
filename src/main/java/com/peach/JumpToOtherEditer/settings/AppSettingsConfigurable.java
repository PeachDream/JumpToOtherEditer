package com.peach.JumpToOtherEditer.settings;

import com.intellij.openapi.options.Configurable;
import com.peach.JumpToOtherEditer.model.EditorConfig;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * 设置页面配置器。
 * 处理设置页面的创建、修改检测、应用和重置操作。
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    @Override
    public String getDisplayName() {
        return "JumpToOtherEditer";
    }

    @Override
    public @Nullable JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        if (mySettingsComponent == null) {
            return false;
        }

        AppSettingsState settings = AppSettingsState.getInstance();
        List<EditorConfig> currentEditors = mySettingsComponent.getEditors();
        List<EditorConfig> savedEditors = settings.editors;

        // 检查列表是否有差异
        if (currentEditors.size() != savedEditors.size()) {
            return true;
        }

        for (int i = 0; i < currentEditors.size(); i++) {
            if (!currentEditors.get(i).equals(savedEditors.get(i))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void apply() {
        if (mySettingsComponent == null) {
            return;
        }

        AppSettingsState settings = AppSettingsState.getInstance();
        settings.setEditors(mySettingsComponent.getEditors());
    }

    @Override
    public void reset() {
        if (mySettingsComponent == null) {
            return;
        }

        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setEditors(settings.copyEditors());
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
