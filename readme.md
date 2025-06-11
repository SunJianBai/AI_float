# 北京理工大学计算机学院《Android技术开发基础》结课设计文档


---

### **一、App的运行与开发环境**

**（1）** **运行环境**： 11.0以上版本Android的Android手机/平板

**（2）** **部署方法**：安装AIFloat.apk（我制作的app）

​                  	安装AnkiDroid （单词卡软件：[ankidroid/Anki-Android: AnkiDroid: Anki flashcards on Android. Your secret trick to achieve superhuman information retention.](https://github.com/ankidroid/Anki-Android)）

**（3）** **开发环境：** Android Studio 2024.3.2

**（4）** **手写代码行数：** 手机端约3500行




---

### **二、App功能说明**

AnkiDroid 是一个我日常使用的学习记忆卡片，但是在记录单词时有点麻烦。所以我开发的这个app可以通过询问ai来自动生成单词卡。

由于考虑到平时在看到陌生词汇可能会出现在不同情境下，所以选择通过悬浮窗的形式来向ai提问。

在主界面上方可以设置ai相关的参数，在外部的悬浮窗中可以与ai交流，输入单词，智能输出anki格式的文本，可以一键将文本导入ankidroid中，也能保存临时卡片，保存的临时卡片可以在主界面看到，能够滑动临时卡片来删除或导入ankidroid。

```
App 主功能
├── 词卡生成功能
│   ├── 单词输入（用户输入待学习单词或词组）
│   ├── 使用 AI 生成卡片内容
│   │   ├── 查询单词释义
│   │   ├── 生成例句
│   │   ├── 补充词性、语境、变形等
│   └── 支持富文本编辑（如加粗、斜体、分层结构）
│
├── 卡片保存功能
│   ├── 使用 Room 数据库存储卡片内容
│   ├── 支持卡片列表管理（查看、编辑、删除）
│   └── 自动记录创建时间、修改时间
│
├── AnkiDroid 导入功能
│   ├── 与 AnkiDroid API 对接
│   ├── 指定目标牌组
│   ├── 一键导入生成的卡片
│   └── 显示导入成功状态或失败信息
│
├── 悬浮窗功能
│   ├── 显示悬浮按钮
│   ├── 在任何界面快速唤起生成器
│   └── 支持在浮窗中输入单词、生成卡片并导入
│
├── 设置与自定义功能
│   ├── 设置默认卡片模板（如正反面结构）
│   ├── 配置 OpenAI API Key
│   ├── 设置默认导入牌组
│   └── 自定义富文本风格选项
```



<img src="https://raw.githubusercontent.com/SunJianBai/pictures/main/img/202506071925306.png" alt="image-20250607192526174" style="zoom: 25%;" />     <img src="https://raw.githubusercontent.com/SunJianBai/pictures/main/img/202506071925232.png" alt="image-20250607192543134" style="zoom:25%;" />    <img src="https://raw.githubusercontent.com/SunJianBai/pictures/main/img/202506071926981.png" alt="image-20250607192626671" style="zoom:25%;" />    <img src="https://raw.githubusercontent.com/SunJianBai/pictures/main/img/202506071926460.png" alt="image-20250607192657955" style="zoom:25%;" />

<img src="https://raw.githubusercontent.com/SunJianBai/pictures/main/img/202506071927789.png" alt="image-20250607192719595" style="zoom:25%;" />   一键导入ankidroid中后会自动生成单词卡  <img src="https://raw.githubusercontent.com/SunJianBai/pictures/main/img/202506071927508.png" alt="image-20250607192746392" style="zoom:25%;" />




---

### **三、App架构设计及技术实现方案**

![image-20250607111429166](https://raw.githubusercontent.com/SunJianBai/pictures/main/img/202506071114249.png)

#### 1. 类关系解析

- **继承关系**：
    - `MainActivity` 继承 `ComponentActivity`
    - `CardEntity` 继承 `Room` 的 `Entity` 注解类

- **组合关系**：
    - `AppDatabase` 组合 `CardDAO` 接口
    - `MainViewModel` 组合 `CardRepository`

- **接口实现**：
    - `CardRepository` 实现 `Repository` 接口
    - `AiService` 是抽象接口，由 `DefaultAiService` 实现

#### 2. 关键技术栈

- **MVVM架构**：
    - `ViewModel` 持有 `StateFlow` → `UI` 收集状态更新 → 用户操作触发 `ViewModel` 方法 → 通过 `Repository` 更新 `Model`
- **Jetpack Compose**：
    - 使用 `LazyColumn` 替代 RecyclerView
    - `Scaffold` 实现Material3布局
- **Room数据库**：
    - `CardDAO` 提供增删改查操作
    - `AppDatabase` 通过 `Room.databaseBuilder` 创建实例
- **Koin依赖注入**：
    - `Module.kt` 定义依赖注入规则
    - `LocalViewModelProvider()` 实现ViewModel注入

#### 3. 第三方组件与自定义组件

- **第三方组件**：
    - **Jetpack Compose**：`androidx.compose.material3` 提供所有UI组件
    - **Room数据库**：`androidx.room` 实现数据持久化
    - **Koin**：`org.koin` 实现轻量级依赖注入
    - **OpenAI SDK**：`com.aallam.openai` 调用AI API.[aallam/openai-kotlin: OpenAI API client for Kotlin with multiplatform and coroutines capabilities.](https://github.com/Aallam/openai-kotlin)
    - **AnkiDroid**：调用AnkiDroid的api将生成的anki格式的单词卡直接导入Anki中。[**Wiki-Ankidroid/AnkiDroid-API.md at master · lonewolf2208/Wiki-Ankidroid**](https://github.com/lonewolf2208/Wiki-Ankidroid/blob/master/AnkiDroid-API.md)
    - **富文本渲染**：解析 Markdown 字符串（如 `# 标题` 转换为标题样式）。依赖组件： richeditor

- **自定义组件**：
    - **悬浮窗服务**：`ComposeOverlayViewService` 和 `OverlayServiceManager` 实现自定义悬浮窗
    - **意图解析器**：`IntentResolver` 封装分享到Anki逻辑
    - **富文本编辑器**：自定义 `RichTextState` 支持Markdown格式
    - **状态管理**：`AiScreenState` 和 `PreferencesUiState` 实现状态绑定

---
