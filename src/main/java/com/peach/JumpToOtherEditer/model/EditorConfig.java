package com.peach.JumpToOtherEditer.model;

import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.Attribute;

import java.util.Objects;
import java.util.UUID;

/**
 * 表示单个外部编辑器的配置。
 * 所有这些编辑器都基于 VS Code，因此它们共享相同的命令行语法。
 */
@Tag("editor")
public class EditorConfig {

    @Attribute("id")
    public String id;

    @Attribute("name")
    public String name;

    @Attribute("command")
    public String command;

    @Attribute("path")
    public String path;

    @Attribute("enabled")
    public boolean enabled;

    @Attribute("builtin")
    public boolean builtin; // 内置编辑器无法删除，只能禁用

    /**
     * XML 序列化需要的默认构造函数
     */
    public EditorConfig() {
        this.id = UUID.randomUUID().toString();
        this.name = "";
        this.command = "";
        this.path = "";
        this.enabled = true;
        this.builtin = false;
    }

    /**
     * 创建新的编辑器配置
     * 
     * @param name    编辑器的显示名称
     * @param command 命令行命令（如 "code", "cursor"）
     * @param builtin 是否为内置编辑器
     */
    public EditorConfig(String name, String command, boolean builtin) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.command = command;
        this.path = "";
        this.enabled = true;
        this.builtin = builtin;
    }

    /**
     * 使用所有字段创建新的编辑器配置
     */
    public EditorConfig(String id, String name, String command, String path, boolean enabled, boolean builtin) {
        this.id = id;
        this.name = name;
        this.command = command;
        this.path = path;
        this.enabled = enabled;
        this.builtin = builtin;
    }

    /**
     * 创建此配置的深拷贝
     */
    public EditorConfig copy() {
        return new EditorConfig(this.id, this.name, this.command, this.path, this.enabled, this.builtin);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EditorConfig that = (EditorConfig) o;
        return enabled == that.enabled &&
                builtin == that.builtin &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(command, that.command) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, command, path, enabled, builtin);
    }

    @Override
    public String toString() {
        return "EditorConfig{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", command='" + command + '\'' +
                ", path='" + path + '\'' +
                ", enabled=" + enabled +
                ", builtin=" + builtin +
                '}';
    }
}
