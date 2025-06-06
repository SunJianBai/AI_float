以下是你提供的 [README.md](file://D:\Codes\Android\AiDict-main\README.md) 文件内容的中文翻译：

---


# AiDict

AiDict 是一款基于 AI 的词典应用，旨在通过即时提供全面的单词和短语解释来优化语言学习体验。与传统词典相比，AiDict 自动整合相关信息并无缝集成到用户的工作流程中，减少手动搜索多个来源的时间。

## 📦 下载

点击下方按钮下载最新版本的应用程序：

[![Download APK](https://img.shields.io/badge/Download-APK-brightgreen?style=for-the-badge&logo=android)](https://github.com/BasetEsmaeili/AiDict/releases/latest/download/app-release.apk)

## 🚀 为什么选择 AiDict？

虽然 AI 聊天机器人可以提供详细的单词解释，但它们需要切换上下文、手动复制以及额外的时间。AiDict 消除了这些摩擦点，提供以下功能：

- **一键单词查询**：在一个地方获取详细的单词含义、同义词、反义词、搭配、例句和词源。
- **无缝集成 Anki**：自动格式化并导出单词解释至 Anki，实现高效的间隔重复学习。
- **AI 驱动的洞察**：结合可信词典使用 AI 模型，提供最准确和全面的结果。

## 🎯 项目目标

AiDict 是一个个人开源项目，旨在通过减少耗时的操作来优化英语学习体验。它并非为商业用途而设计，而是为了解决个人需求。**任何人不得将此项目用于商业用途。**

## 🛠️ 技术栈

### 📱 Android 框架

- **语言**：Kotlin（使用 Coroutines 处理异步任务）
- **UI 框架**：Jetpack Compose（构建现代声明式 UI）
- **数据库**：Room DB（用于临时存储卡片信息）
- **偏好管理**：Data Store（用于存储用户设置和偏好）
- **设计系统**：Material 3（提供现代化且可访问的 UI）
- **富文本编辑**：[Compose Rich Editor](https://github.com/MohamedRejeb/compose-rich-editor)（用于高级文本输入和格式化）
- **图片加载**：Coil（高效处理图像）
- **依赖注入**：Koin（轻量级依赖管理）

### 🤖 AI 集成

- **AI 服务**：OpenAI & DeepSeek（用于智能单词分析和定义）

## 📌 功能特性

- 🔍 **即时单词查询**：AI 提供定义、同义词、反义词、示例用法和发音。
- 📊 **Anki 导出**：自动将 AI 响应格式化为 Anki 闪卡。
- 🔧 **可配置的 AI 设置**：在不同 AI 模型间选择以提高成本效益。

## 🏗️ 开发状态

目前，AiDict 处于早期开发阶段。该项目最初使用 Gemini AI 构建，但现在正逐步迁移到 OpenAI 和 DeepSeek 以获得更好的定制性和效率。

## 📸 截图

## 🔮 未来计划

- 🔄 **~~完成从 Gemini 到 OpenAI 和 DeepSeek 的迁移~~**（已完成）
- 💰 **利用 OpenAI 批处理服务降低成本**
- 🔗 **集成来源和搜索功能以获得更准确的 AI 结果**
- 🎯 **微调 AI 模型以节省 Token 并提高准确性**
- 📂 **从 CSV 文件导入并处理单词**
- 🎤 **实现语音转文本服务**
- 🔊 **朗读答案以帮助发音学习**
- 🔍 **在主页添加 AI 搜索功能**
- 📸 **添加类似圆形搜索的功能**

## 🤝 贡献

尽管这主要是我个人的项目，但我们始终欢迎贡献、反馈和讨论。欢迎你自由探索、分叉并改进这个仓库！


## 🧾 贡献者许可协议 (CLA)

通过向本仓库提交代码、想法或文档，你同意以下条款：

- 你的贡献是在与本项目相同的许可证（MIT + 非商业）下提供的。
- 你授予项目维护者完全权利，在项目中使用、修改和分发你的贡献，并遵循项目的现有许可证。

---

### ⚠️ 免责声明

本项目仍在持续开发中，重点在于功能性而非完美无缺。尽管我努力遵循最佳实践，但仍有许多改进空间。
如果你发现有待改进之处，欢迎提出建议或进行贡献——非常感谢你的支持！
我的主要目标是高效地开发这款应用程序，并在此过程中提升自己的语言学习体验。
感谢你的支持，祝你编码愉快！




以下是项目中各文件的详细中文注释说明：

一、基础模块 (common)
1. Constants.kt
```kotlin
// 全局常量定义
// 包含请求超时时间、温度参数、SharedPreferences键值等全局配置
// 示例：REQUEST_TIME_OUT_DEFAULT_VALUE = 30_000L // 默认请求超时时间30秒
```


二、数据层 (data)
1. 数据库模块 (db)
- AppDatabase.kt：Room数据库主类，定义数据库实体和版本号
- CardDAO.kt：卡片数据访问对象，包含增删改查的DAO操作
- CardEntity.kt：数据库实体类，对应卡片表结构定义

2. 依赖注入 (di)
- Module.kt：Koin依赖注入模块，提供数据库、仓库等实例

3. 偏好设置 (pref)
- PreferenceHelper.kt：SharedPreferences工具类，封装基本类型数据的读写操作

4. 仓库实现
- CardRepositoryImpl.kt：卡片仓库实现类，实现领域层定义的CardRepository接口
- PreferenceRepositoryImpl.kt：偏好设置仓库实现，处理应用配置持久化

三、领域层 (domain)
1. 实体类 (entity)
- Card.kt：卡片实体类，定义front/back字段和数据库映射关系

2. 接口定义
- CardRepository.kt：卡片数据操作接口，定义业务所需的数据方法
- PreferenceRepository.kt：偏好设置接口，抽象化配置管理方法

四、表现层 (presentation)
1. 依赖注入 (di)
- Module.kt：ViewModel、Service等组件的Koin注入配置

2. 服务模块 (service)
- ComposeOverlayViewService.kt：悬浮窗服务基类，提供Compose UI的悬浮窗实现
- OverlayServiceManager.kt：悬浮窗服务管理器，处理服务生命周期
- ShortcutWindowService.kt：快捷方式窗口服务，处理桌面快捷入口
- ViewReadyService.kt：视图就绪监听服务，确保UI加载完成

3. UI模块 (ui)
   a. AI交互模块 (ai)
- AiActivity.kt：AI功能主Activity，处理权限请求和基础UI容器
- AiScreen.kt：AI功能Compose UI实现，包含对话界面和状态管理
- AiViewModel.kt：AI功能ViewModel，管理对话状态和业务逻辑
- model/目录：
    - PickedMedia.kt：媒体选择数据模型，封装图片URI
    - UiMode.kt：界面状态枚举类，定义Ask/Answer/Loading/Error四种模式

b. 核心组件 (core)
- component/目录：
    - AiDialog.kt：通用对话框组件
    - AppLogo.kt：应用LOGO组件，支持点击事件和自定义样式
    - InputDialogPreferenceItem.kt：输入设置对话框组件
    - OptionDialogPreferenceItem.kt：选项设置对话框组件
    - RadioOptionDialog.kt：单选对话框组件
    - RadioPreferenceItem.kt：单选列表项UI组件
    - SwitchPreferenceItem.kt：开关设置项UI组件

- model/目录：
    - UiText.kt：UI文本封装类，支持字符串资源和动态文本

- modifier/目录：
    - Conditional.kt：条件修饰符，根据条件应用不同修饰
    - EnableOrDisableAlpha.kt：透明度控制修饰符
    - LinearGradientAnimation.kt：线性渐变动画修饰符

- theme/目录：
    - Color.kt：颜色常量定义
    - Dimen.kt：尺寸常量定义
    - Theme.kt：主题配置，定义MaterialTheme扩展
    - Type.kt：字体类型配置

c. 主界面模块 (main)
- ExportType.kt：导出类型枚举，定义HTML/Markdown/PLAIN三种格式
- MainActivity.kt：应用主入口Activity
- MainScreen.kt：主界面Compose UI实现，包含卡片列表和操作栏
- MainUiState.kt：主界面状态类，管理卡片列表和偏好设置状态
- MainViewModel.kt：主界面ViewModel，处理数据加载和业务逻辑
- OptionItem.kt：选项条目类，支持单选/多选状态管理
- PreferenceItem.kt：偏好设置项基类，定义各种设置项类型
- PreferenceList.kt：偏好设置列表组件，统一管理设置项UI
- PreferencesUiState.kt：偏好设置UI状态持有类

4. 工具类 (util)
- AndroidSdkExt.kt：Android SDK扩展函数，包含版本判断等工具方法
- ClipboardManager.kt：剪贴板管理类，实现文本复制功能
- IntentResolver.kt：意图解析类，处理分享、创建Anki卡片等Intent操作
- KotlinSugarExt.kt：Kotlin语法糖扩展，包含集合操作等工具方法
- LifecycleAwareSpeechRecognizer.kt：生命周期感知的语音识别器
- NetworkMonitor.kt：网络状态监控类，提供网络可用性观察
- NotificationsHelper.kt：通知管理类，处理悬浮窗权限提示等通知
- ResourceExt.kt：资源扩展函数，包含字符串格式化等工具
- ResourceProvider.kt：资源提供者，统一管理字符串资源获取
- UriConverter.kt：URI转换器，实现内容URI到字节数组的转换

五、工作管理 (worker)
- UniqueIdGeneratorWorker.kt：唯一ID生成工作类，使用WorkManager执行后台任务

六、应用入口
- ApplicationLoader.kt：Application初始化类，配置全局依赖注入和初始化逻辑

注释特点说明：
1. 分层结构：按模块划分注释，突出MVVM架构特点
2. 技术栈标注：标明使用Jetpack Compose、Room、Koin等技术
3. 组件关系：说明各组件间的依赖关系（如ViewModel与Repository）
4. 关键逻辑：标注重要功能实现方式（如Anki卡片创建、悬浮窗实现）
5. 扩展性提示：指出可扩展点（如支持更多AI服务提供商）

建议使用方式：
1. 新开发者：从ApplicationLoader开始了解启动流程
2. 功能扩展：查看对应模块的ViewModel和Repository实现
3. UI修改：定位到对应的Compose组件文件
4. 新增功能：参考现有模块的接口设计模式

注：由于代码片段不完整，部分注释基于Android开发通用模式推测，实际注释应结合完整代码内容进行精确标注。