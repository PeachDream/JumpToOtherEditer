plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.2.1"
}

group = "com.peach"
version = "1.2.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.3")
        
        // Add plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "243"
            untilBuild = provider { null } // No upper bound, compatible with future versions
        }

        changeNotes = """
            <h3>1.2.0</h3>
            <ul>
                <li><b>Dynamic Editor Configuration</b> - Add, remove, and configure custom editors!</li>
                <li>New built-in editors: Windsurf, Trae CN</li>
                <li>Support for all JetBrains IDEs</li>
                <li>New table-based settings UI</li>
                <li>Improved error messages</li>
            </ul>
            <h3>1.1.0</h3>
            <ul>
                <li>Added CodeBuddy editor support</li>
                <li>Added tips panel in settings</li>
                <li>Fixed PATH detection issues</li>
            </ul>
            <h3>1.0.0</h3>
            <ul>
                <li>Initial release</li>
                <li>Support for VS Code, Cursor, Antigravity, Qoder, Trae</li>
            </ul>
        """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
