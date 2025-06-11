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



## **四.技术亮点、技术难点及其解决方案**

现在安卓端没有找到好用的制作anki单词卡的工具，需要手动复制来制作。

**我的 App 的独特之处：**

- **AI 自动生成内容**：用户只需输入一个单词，系统即可调用 AI 自动生成 Anki 格式的卡片（包含释义、例句、词性等）。

- **悬浮窗设计**：可以在任意 App 中快速呼出悬浮窗输入单词，极大提升了使用场景的灵活性。

- **一键导入 AnkiDroid**：通过 AnkiDroid 提供的 API 直接将卡片添加到本地卡组，无需手动操作或导出。



我最得意的功能是：在悬浮窗中与 AI 交互并生成卡片，同时还能一键导入到 AnkiDroid 中。

- **设计初衷**：很多时候我们遇到一个陌生词是在刷网页、看 PDF 或聊天中，如果只能在主界面操作，很不方便。
- **实现效果**：悬浮窗支持快速输入并调用 AI，无论用户在哪个 App 中看到一个陌生单词，都能随时打开悬浮窗进行操作，不需要切屏才能查单词。并且查询单词后可以一键导入到ankidroid中。



**技术难点：**

**与 AnkiDroid 的导入接口集成不顺利**

我希望用户能一键把卡片导入到 AnkiDroid，但这个 API 是通过内容提供器（ContentProvider）开放的，而且文档不是很多。我一开始传参方式错了，总是导入失败。后来仔细查了 AnkiDroid 的 GitHub 示例代码，才学会正确构造卡片格式，并正确处理返回的导入结果。最终实现了一键导入功能，算是我这个项目最有成就感的地方之一了。

**OpenAI 接口对接**

我在接入 OpenAI 的接口时，一开始不知道怎么构造 prompt，返回的内容也不是我想要的格式，比如有时候它只返回了单词，没有例句或者词性，这让我在做卡片展示时很抓狂。后来我尝试自己总结了一些 prompt 模板，像是 “请用 Anki 格式生成单词卡片，包含词性、解释和英文例句”，这样让结果更稳定。
还有就是返回的数据是异步的，我在 UI 页面用的是 Compose，一开始不知道怎么在页面里“等结果”，后来才学会在 ViewModel 里配合 `MutableStateFlow` 做状态管理，才能正确显示出来。

**MVVM架构**

Jetpack Compose 是我第一次用，一开始觉得很方便，能直接写界面，但后来发现所有逻辑都堆在 UI 代码里，导致功能一多就很乱。我才意识到应该用 MVVM 架构，把数据处理放在 ViewModel 中。我花了不少时间去看别人的项目结构，也参考了 Google 官方示例，慢慢把卡片生成、存储、导入这些功能拆分到不同层，这样代码清晰了很多。



## **五.简要开发过程**

5月16号  查找资料，确定要开发的目标

5月19号  完成系统设计

5月26号  基本框架编写

5月30号  调用openai的api

5月31号  悬浮窗ui编写

5月32号  设置界面ui编写

6月2号   调用AnkiDroid的api

6月3号   主界面卡片编写

6月9号	对程序进行集成测试

6月11号  程序开发工作完毕，编写及整理文档



## 六. 学习感悟及对本课程的建议

​	这门课是我第一次真正从头到尾做一个完整的 App，虽然过程中遇到了很多问题，但回头看，其实是非常有收获的一段经历。

刚开始学的时候，其实我对 Android 的开发流程几乎是零基础，很多概念都挺抽象的。PPT上讲的理论有些我当时没完全理解，但在实际开发过程中，我慢慢发现——以前听不懂的概念，其实在做项目时一个个都能“活起来”。比如我一开始不太理解“MVVM 分层的意义”，但当我写到功能多了、代码混乱了以后，才意识到分层结构有多重要。

我觉得亲自动手做项目是理解理论最好的方式。比如异步调用、数据状态管理、权限请求这些，看ppt过了一遍但是写的时候就想不起来。有时候调 bug 会花几个小时甚至几天，但解决后真的成就感满满。

**对课程教学的建议是：**

​	可以给点混合APP和H5的资料，感觉原生app太复杂了。Web APP会更好写一点。

​	另外，如果能提前给我们演示几个完整但结构清晰的小项目样例就更好了。我们很多人其实是边写边学的，如果有清晰的项目参考，能让我们少走一些弯路，也更好地理解“写一个好项目”的标准。

**AI使用情况：**

1. 项目构思阶段，借助 AI 梳理思路

2. 编码阶段，AI 帮我写了很多关键代码

   很多模块，比如：

    - 怎么用 Jetpack Compose 写一个输入框+按钮的 UI
    - 怎么调用 OpenAI 的 API、处理异步请求
    - 怎么使用 Room 数据库保存卡片内容
    - 怎么和 AnkiDroid API 通信导入卡片

   这些我一开始真的完全不会，都是我边问 AI，边把它提供的代码复制进项目里，然后再改改跑一下。有时候它会直接给出完整的代码片段，还会解释“这段代码是干嘛的、参数是什么意思”，对我这种新手非常友好。

   尤其是在处理富文本渲染、悬浮窗服务、Markdown 转换这些复杂功能时，AI 帮我快速找到了方向，并且还指出了很多容易忽略的问题（比如权限申请、后台服务的生命周期等）。

3. 调试阶段

   ​	调试过程中报错太多了，有些错误我根本看不懂。每次遇到崩溃或者运行失败，我会把报错信息贴给 AI，它一般能很快帮我定位到问题，比如某个 API 使用错了，或者是忘记配置权限，甚至是版本不兼容的问题。

   ​	此外，在样式优化、架构调整上我也请教了 AI，比如我后来想从原来的结构改成 MVVM 架构，也是请 AI 帮我分析各个部分该放哪，ViewModel 应该怎么用，状态流要怎么管理。



总之，这门课让我从一个完全没做过 App 的新手，变成了能独立开发一个完整 App 的“准开发者”，我很感激，也很开心看到自己的成长。如果以后还能有更多项目驱动的课程，我一定会继续坚持下去，继续提升自己！



