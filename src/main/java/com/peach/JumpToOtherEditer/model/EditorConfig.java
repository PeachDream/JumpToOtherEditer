package com.peach.JumpToOtherEditer.model;

import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.Attribute;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents configuration for a single external editor.
 * All these editors are VS Code based, so they share the same CLI syntax.
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
    public boolean builtin; // Built-in editors cannot be deleted, only disabled

    /**
     * Default constructor required for XML serialization
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
     * Create a new editor configuration
     * 
     * @param name    Display name of the editor
     * @param command CLI command (e.g., "code", "cursor")
     * @param builtin Whether this is a built-in editor
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
     * Create a new editor configuration with all fields
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
     * Create a deep copy of this configuration
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
