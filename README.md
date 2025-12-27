# JumpToOtherEditer

<p align="center">
  <img src="https://img.shields.io/badge/JetBrains-Plugin-blue?style=for-the-badge&logo=jetbrains" alt="JetBrains Plugin"/>
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Version-1.2.0-brightgreen?style=for-the-badge" alt="Version"/>
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License"/>
</p>

<p align="center">
  <b>🚀 一键从 JetBrains IDE 跳转到任意外部 AI 编辑器</b><br/>
  <i>One-click jump from JetBrains IDE to any external AI editor</i>
</p>

---

## 📖 简介 / Introduction

**JumpToOtherEditer** 是一款强大的 JetBrains IDE 插件，让你可以快速在外部 AI 编辑器中打开当前文件，并自动定位到光标所在行。

**完全自定义！** 你可以自由添加任何基于 VS Code 的编辑器。

A powerful JetBrains IDE plugin that allows you to quickly open the current file in external AI editors and automatically navigate to the cursor position. **Fully customizable!** You can add any VS Code-based editor.

---

## ✨ 功能特性 / Features

| 功能 | 说明 |
|------|------|
| 🚀 **一键跳转** | 右键菜单快速打开文件到外部编辑器 |
| 📍 **精确定位** | 自动跳转到当前光标所在行 |
| 📁 **项目模式** | 以项目方式打开，保留完整的目录结构 |
| 🔧 **动态配置** | 可自由添加、删除、配置编辑器 |
| ➕ **自定义编辑器** | 添加任意 VS Code 风格的编辑器 |
| 🌐 **自动检测** | 智能检测编辑器安装路径（Windows） |
| 🎯 **全平台IDE** | 支持所有 JetBrains IDE |
| 💻 **跨系统** | 支持 Windows / macOS / Linux |

---

## 🖥️ 支持的 IDE / Supported IDEs

本插件支持**所有 JetBrains IDE**（2024.3 及以上版本）：

<table>
<tr>
<td>

- ✅ IntelliJ IDEA (Community / Ultimate)
- ✅ PyCharm (Community / Professional)
- ✅ WebStorm
- ✅ GoLand
- ✅ CLion

</td>
<td>

- ✅ Rider
- ✅ PhpStorm
- ✅ RubyMine
- ✅ DataGrip
- ✅ Android Studio

</td>
</tr>
</table>

---

## 🎯 内置编辑器 / Built-in Editors

开箱即用，支持以下主流 AI 编辑器：

| 编辑器 | 命令 | 描述 |
|--------|------|------|
| **VS Code** | `code` | Microsoft Visual Studio Code |
| **Cursor** | `cursor` | Cursor AI Editor |
| **Antigravity** | `antigravity` | Antigravity AI Editor |
| **Windsurf** | `windsurf` | Codeium AI Editor |
| **Trae** | `trae` | 字节跳动 AI 编辑器（国际版） |
| **Trae CN** | `trae-cn` | 字节跳动 AI 编辑器（中国版） |
| **CodeBuddy** | `buddy` | 腾讯 AI 编程助手 |

> 💡 **提示**：所有这些编辑器都基于 VS Code，使用相同的命令行语法 `command -r -g <file>:<line>`

---

## 📦 安装 / Installation

### 方法一：从 Marketplace 安装（推荐）

1. 打开你的 JetBrains IDE
2. 进入 `Settings/Preferences` → `Plugins` → `Marketplace`
3. 搜索 **JumpToOtherEditer**
4. 点击 **Install** 安装

### 方法二：手动安装

1. 从 [Releases](https://github.com/your-username/JumpToOtherEditer/releases) 下载最新的 `.zip` 插件包
2. 打开你的 JetBrains IDE
3. 进入 `Settings/Preferences` → `Plugins`
4. 点击齿轮图标 ⚙️ → `Install Plugin from Disk...`
5. 选择下载的 `.zip` 文件

---

## 🚀 使用方法 / Usage

### 基本使用

1. 在编辑器中打开任意文件
2. **右键点击** 编辑区域或项目文件
3. 选择 **Jump to External Editor** 菜单
4. 选择你想要跳转的目标编辑器

```
📁 Right-click Context Menu
└── 🔌 Jump to External Editor
    ├── Open in VS Code
    ├── Open in Cursor
    ├── Open in Antigravity
    ├── Open in Windsurf
    ├── Open in Trae
    ├── Open in Trae CN
    ├── Open in CodeBuddy
    └── ... (你的自定义编辑器)
```

### 工作原理

- 文件会自动以**项目模式**打开（包含完整目录树）
- 光标会自动**定位到当前行**
- 插件会智能查找项目根目录（通过 `.git`, `.idea`, `package.json` 等标记）
- 支持在编辑器、项目视图和标签页中使用

---

## ⚙️ 配置 / Configuration

进入 `Settings/Preferences` → `Tools` → `JumpToOtherEditer` 进行配置。

### 设置界面

设置页面采用现代化的表格界面，支持：

| 列名 | 说明 | 可编辑 |
|------|------|--------|
| **启用/Enable** | 是否在右键菜单中显示 | ✅ |
| **名称/Name** | 编辑器显示名称 | ✅ (自定义编辑器) |
| **命令/Command** | CLI 命令（如 `code`, `cursor`） | ✅ (自定义编辑器) |
| **路径/Path** | 可执行文件路径（留空使用 PATH） | ✅ |
| **内置/Built-in** | 是否为内置编辑器 | ❌ |

### 操作按钮

- **➕ 添加** - 添加自定义编辑器
- **➖ 删除** - 删除自定义编辑器（内置编辑器无法删除，只能禁用）

---

## ➕ 添加自定义编辑器 / Custom Editors

你可以添加任何基于 VS Code 的编辑器！

### 添加步骤

1. 进入 `Settings` → `Tools` → `JumpToOtherEditer`
2. 点击 **"+"** 按钮
3. 输入编辑器名称（显示在菜单中）
4. 输入命令名称（如 `myeditor`）
5. (可选) 双击路径列配置可执行文件路径

### 示例

添加一个自定义的 "MyEditor" 编辑器：

```
名称: MyEditor
命令: myeditor
路径: C:\Program Files\MyEditor\bin\myeditor.cmd
```

> 💡 自定义编辑器必须支持 VS Code 风格的命令行参数：
> ```bash
> myeditor -r -g "filepath:line"
> ```

---

## 💡 使用提示 / Tips

### 1. 安装编辑器时添加到 PATH

安装编辑器时，请勾选 **"Add to PATH"** 选项。这样插件就能直接通过命令名启动编辑器。

### 2. 重启 IDE 更新环境变量

如果你在打开 IDE **之后**才安装了编辑器，需要**重启 IDE** 以获取最新的 PATH 环境变量。

### 3. 手动配置路径

如果命令无法识别，可以在设置中手动配置编辑器的可执行文件路径。

#### Windows 常见路径示例

| 编辑器 | 默认路径 |
|--------|----------|
| VS Code | `%LOCALAPPDATA%\Programs\Microsoft VS Code\bin\code.cmd` |
| Cursor | `%LOCALAPPDATA%\Programs\cursor\resources\app\bin\cursor.cmd` |
| Windsurf | `%LOCALAPPDATA%\Programs\Windsurf\bin\windsurf.cmd` |
| Trae | `%LOCALAPPDATA%\Programs\Trae\bin\trae.cmd` |
| Trae CN | `%LOCALAPPDATA%\Programs\Trae CN\bin\trae-cn.cmd` |
| CodeBuddy | `%LOCALAPPDATA%\Programs\CodeBuddy\bin\buddy.cmd` |

### 4. Trae 特别说明

**Trae** 首次启动时，需要点击 **"安装 trae 命令"** 按钮才能通过命令行启动。

---

## ❓ 常见问题 / FAQ

<details>
<summary><b>Q: 点击菜单后没有反应？</b></summary>

**A:** 请检查：
1. 目标编辑器是否已正确安装
2. 编辑器命令是否已添加到 PATH
3. 如果是先开 IDE 后装编辑器，请重启 IDE
4. 在设置页面手动配置可执行文件路径

</details>

<details>
<summary><b>Q: 提示 "xxx" 不是内部或外部命令？</b></summary>

**A:** 编辑器未添加到 PATH。解决方法：
1. 在插件设置中手动配置可执行文件路径
2. 或者重新安装编辑器，勾选 "Add to PATH" 选项
3. 重启 IDE 后重试

</details>

<details>
<summary><b>Q: 如何添加新的编辑器支持？</b></summary>

**A:** 进入设置页面，点击 **"+"** 按钮即可添加任意 VS Code 风格的编辑器。只需提供名称和命令即可！

</details>

<details>
<summary><b>Q: macOS / Linux 支持吗？</b></summary>

**A:** 支持！插件基于通用的命令行方式调用编辑器。macOS/Linux 用户确保编辑器命令在 PATH 中即可。

</details>

<details>
<summary><b>Q: 为什么右键菜单中有些编辑器没有显示？</b></summary>

**A:** 可能该编辑器被禁用了。进入设置页面，确保对应编辑器的 "启用" 复选框已勾选。

</details>

---

## 🛠️ 开发 / Development

### 环境要求

- JDK 21+
- IntelliJ IDEA 2024.1+
- Gradle 8.0+

### 构建

```bash
# 克隆项目
git clone https://github.com/your-username/JumpToOtherEditer.git

# 进入目录
cd JumpToOtherEditer

# 构建插件
./gradlew build

# 运行测试 IDE
./gradlew runIde
```

### 项目结构

```
src/main/java/com/peach/jumptoantigravity/
├── actions/
│   └── JumpToToolsGroup.java      # 动态右键菜单组
├── model/
│   └── EditorConfig.java          # 编辑器配置数据模型
├── services/
│   └── AntigravityLauncher.java   # 编辑器启动服务（含路径检测）
└── settings/
    ├── AppSettingsComponent.java   # 设置页面 UI（表格式）
    ├── AppSettingsConfigurable.java # 设置页面配置器
    └── AppSettingsState.java       # 设置持久化状态
```

### 核心类说明

| 类名 | 功能 |
|------|------|
| `JumpToToolsGroup` | 动态生成右键菜单，根据配置显示已启用的编辑器 |
| `EditorConfig` | 编辑器配置模型，包含名称、命令、路径、启用状态等 |
| `AntigravityLauncher` | 负责启动外部编辑器，支持路径自动检测和项目根目录查找 |
| `AppSettingsState` | 持久化编辑器配置，管理内置和自定义编辑器 |
| `AppSettingsComponent` | 表格式设置界面，支持添加、删除、编辑编辑器配置 |

---

## 📄 更新日志 / Changelog

### v1.2.0 (2024-12-27)
- ✨ **动态编辑器配置** - 支持自定义添加/删除编辑器
- ✨ 新增 Windsurf、Trae CN 内置支持
- 🖥️ 支持所有 JetBrains IDE
- 🎨 全新表格式设置界面
- 🔍 Windows 下自动检测编辑器安装路径
- 📝 更新文档

### v1.1.0 (2024-12-27)
- ✨ 新增 CodeBuddy 编辑器支持
- 💡 添加使用提示面板
- 🐛 修复 PATH 环境变量检测问题
- 📝 完善文档说明

### v1.0.0
- 🎉 首次发布
- 支持 VS Code, Cursor, Antigravity, Qoder, Trae
- 自动项目检测
- 行号定位

---

## 📜 许可证 / License

本项目采用 [MIT License](LICENSE) 开源许可证。

---

## 🤝 贡献 / Contributing

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📧 联系 / Contact

如有问题或建议，请通过以下方式联系：

- 提交 [GitHub Issue](https://github.com/your-username/JumpToOtherEditer/issues)

---

<p align="center">
  Made with ❤️ by Peach
</p>

<p align="center">
  <i>让AI编辑器与JetBrains IDE无缝协作</i><br/>
  <i>Seamless integration between AI editors and JetBrains IDE</i>
</p>