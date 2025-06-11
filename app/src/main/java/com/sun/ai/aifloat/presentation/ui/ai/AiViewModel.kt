package com.sun.ai.aifloat.presentation.ui.ai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatMessage
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import com.aallam.openai.client.RetryStrategy
import com.sun.ai.aifloat.BuildConfig
import com.sun.ai.aifloat.R
import com.sun.ai.aifloat.common.Constants
import com.sun.ai.aifloat.domain.CardRepository
import com.sun.ai.aifloat.domain.PreferenceRepository
import com.sun.ai.aifloat.domain.entity.Card
import com.sun.ai.aifloat.presentation.ui.ai.model.PickedMedia
import com.sun.ai.aifloat.presentation.ui.ai.model.UiMode
import com.sun.ai.aifloat.presentation.ui.core.model.UiText
import com.sun.ai.aifloat.presentation.ui.main.ExportType
import com.sun.ai.aifloat.presentation.ui.main.PreferenceItem
import com.sun.ai.aifloat.presentation.util.ClipboardManager
import com.sun.ai.aifloat.presentation.util.IntentResolver
import com.sun.ai.aifloat.presentation.util.NetworkMonitor
import com.sun.ai.aifloat.presentation.util.ResourceProvider
import com.sun.ai.aifloat.presentation.util.UriConverter
import com.sun.ai.aifloat.presentation.util.isDaytime
import com.sun.ai.aifloat.presentation.util.toBase64
import com.mohamedrejeb.richeditor.model.RichTextState
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * AI功能视图模型
 * 功能：
 * - 管理AI对话的核心业务逻辑
 * - 处理OpenAI API请求/响应
 * - 维护UI状态
 * 技术栈：
 * - 使用Kotlin协程处理异步操作
 * - 通过Flow实现响应式编程
 * - 集成OpenAI SDK进行API通信
 * 组件关系：
 * - 依赖PreferenceRepository获取用户偏好设置
 * - 通过NetworkMonitor监控网络状态
 * - 使用UriConverter处理URI转换
 * - 调用IntentResolver执行外部意图
 * - 与CardRepository交互实现数据持久化
 */
class AiViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val resourceProvider: ResourceProvider,
    networkMonitor: NetworkMonitor,
    private val uriConverter: UriConverter,
    private val intentResolver: IntentResolver,
    private val clipboardManager: ClipboardManager,
    private val cardRepository: CardRepository
) : ViewModel() {
    private val headlineTitle: UiText = UiText.StringResource(
        R.string.title_what_would_you_like_to_ask,
        resourceProvider.getString(
            if (isDaytime()) {
                R.string.label_today
            } else {
                R.string.label_tonight
            }
        )
    )
    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        uiModeState.value = UiMode.Error
        typeAnswer(e.localizedMessage.orEmpty().trim())
    }
    private val commandTextState = TextFieldState()
    private val answerTextState = RichTextState()

    private val messageEventChannel = Channel<String>(Channel.BUFFERED)
    val messageEventFlow = messageEventChannel.receiveAsFlow()

    private val pickedImageState = MutableStateFlow<PickedMedia?>(null)
    private val makeFullScreenState = MutableStateFlow(false)
    private val uiModeState = MutableStateFlow<UiMode>(UiMode.Ask)
    private val networkState = networkMonitor.networkMonitorFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        NetworkMonitor.State.Unknown
    )

    val windowServiceEnabledPreferenceState = preferenceRepository.getPreference(
        Constants.PreferencesKey.keyWindowServiceEnabled,
        true
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT),
        true
    )

    private val commandOptionsStateFlow = MutableStateFlow(
        Pair<Boolean, PersistentList<PreferenceItem>>(
            false,
            persistentListOf()
        )
    )

    private val selectedAiModel =
        preferenceRepository.getPreference(Constants.PreferencesKey.keySelectedAiModel, "")
            .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val exportType =
        preferenceRepository.getPreference(
            Constants.PreferencesKey.keyExportType,
            ExportType.entries.first().name
        ).map { storedValue ->
            ExportType.entries.first { it.name.contentEquals(storedValue, true) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ExportType.entries.first())

    val uiState =
        combine(
            pickedImageState,
            makeFullScreenState,
            uiModeState,
            commandOptionsStateFlow
        ) { pickedImage,
            makeFullScreenState,
            uiMode,
            commandOptionsState ->
            UiState(
                headlineTitle = headlineTitle,
                commandTextState = commandTextState,
                answerTextState = answerTextState,
                pickedMedia = pickedImage,
                commandOptionsEnabled = commandOptionsState.first,
                commandOptions = commandOptionsState.second,
                makeFullScreen = makeFullScreenState,
                uiMode = uiMode
            )
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT), UiState(
                headlineTitle = headlineTitle,
                commandTextState = commandTextState,
                answerTextState = answerTextState
            )
        )
    private val openAIStateFlow = preferenceRepository.getAllPreferences()
        .map { preferencesMap ->
            val apikey =
                preferencesMap[Constants.PreferencesKey.keyApiKey]?.toString() ?: return@map null
            val timeout = (preferencesMap[Constants.PreferencesKey.keyRequestTimeout] as? Long)
                ?: Constants.PreferencesKey.REQUEST_TIME_OUT_DEFAULT_VALUE
            val host = (preferencesMap[Constants.PreferencesKey.keyHost] as? String)
                ?: Constants.PreferencesKey.defaultHost

            OpenAI(
                OpenAIConfig(
                    token = apikey,
                    logging = LoggingConfig(
                        logLevel = if (BuildConfig.DEBUG) {
                            LogLevel.All
                        } else {
                            LogLevel.None
                        },
                        logger = Logger.Default,
                        sanitize = true
                    ),
                    timeout = Timeout(
                        request = timeout.milliseconds,
                        connect = timeout.milliseconds,
                        socket = timeout.milliseconds
                    ),
                    host = getHostBasedOnSelectedService(host),
                    retry = RetryStrategy(),
                    engine = OkHttpEngine(OkHttpConfig())
                )
            )
        }.flowOn(Dispatchers.IO)
        .filterNotNull()
        .onEach {
            loadAiModels(openAI = it)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private fun loadAiModels(openAI: OpenAI) {
        viewModelScope.launch(exceptionHandler) {
            val models = openAI.models().map { model ->
                PreferenceItem.Radio(
                    id = model.id.id,
                    title = UiText.DynamicString(model.id.id),
                    checked = model.id.id.contentEquals(selectedAiModel.value),
                    onRadioCheckChange = this@AiViewModel::onAiModelSelected
                )
            }.toPersistentList()
            commandOptionsStateFlow.value = Pair(models.isNotEmpty(), models)
        }
    }

    private fun onAiModelSelected(item: PreferenceItem.Radio) {
        viewModelScope.launch {
            preferenceRepository.putPreference(Constants.PreferencesKey.keySelectedAiModel, item.id)
        }
        commandOptionsStateFlow.update { state ->
            Pair(
                state.first,
                state
                    .second
                    .mutate { list ->
                        for (index in list.indices) {
                            list[index] =
                                (list[index] as PreferenceItem.Radio).copy(checked = list[index].id == item.id)
                        }
                    }
            )
        }
    }

    private fun getHostBasedOnSelectedService(host: String): OpenAIHost {
        return when (host) {
            Constants.DefaultContent.OPEN_AI -> OpenAIHost.OpenAI
            Constants.DefaultContent.DEEP_SEEK -> OpenAIHost(Constants.DefaultContent.DEEP_SEEK_BASE_URL)
            else -> throw IllegalArgumentException("Unknown host")
        }
    }

    /**
     * 处理Activity启动时传递的Extras参数
     * 参数：
     * - extras: Bundle类型的附加参数
     */
    fun onExtrasRetrieved(extras: Bundle?) {
        extras ?: return
        uiModeState.value = UiMode.Ask
        val extraText = extras.getString(Intent.EXTRA_TEXT)
        val processText = extras.getString(Intent.EXTRA_PROCESS_TEXT)
        commandTextState.edit {
            append(Constants.SPACE)
            append(processText ?: extraText)
        }
    }

    /**
     * 处理"提问标准"按钮点击事件
     * 功能：用于触发特定的提问标准操作
     */
    fun onAskCriteriaClicked() {
        // 实现具体的提问标准逻辑
    }

    /**
     * 切换全屏显示模式
     * 功能：在全屏和非全屏之间切换
     */
    fun onMakeFulScreenClicked() {
        makeFullScreenState.update { !it }
    }

    /**
     * 处理媒体选择结果
     * 参数：
     * - uri: 选中的媒体文件URI
     */
    fun onPickMediaResult(uri: Uri?) {
        pickedImageState.value = uri?.let { PickedMedia(it) }
    }

    /**
     * 移除已选中的媒体
     * 功能：清除当前选中的媒体文件
     */
    fun onRemovePickedMediaClicked() {
        pickedImageState.value = null
    }

    /**
     * 发送命令按钮点击处理
     * 功能：验证输入并启动AI对话流程
     */
    fun onSendCommandClicked() {
        val openAI = openAIStateFlow.value ?: run {
            messageEventChannel.trySend(resourceProvider.getString(R.string.error_model_config))
            return
        }
        if (networkState.value != NetworkMonitor.State.Available) {
            messageEventChannel.trySend(resourceProvider.getString(R.string.error_check_network))
            return
        }
        if (selectedAiModel.value.isEmpty()) {
            messageEventChannel.trySend(resourceProvider.getString(R.string.error_select_an_ai_model))
            return
        }
        uiModeState.value = UiMode.Loading
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            runCatching {
                startConversation(openAI)
            }.onFailure {
                uiModeState.value = UiMode.Error
                typeAnswer(it.message.orEmpty().trim())
            }
        }
    }

    /**
     * 准备默认指令
     * 功能：构建发送给AI的系统指令
     * 返回值：包含所有指令的字符串
     */
    private suspend fun prepareDefaultInstructions(): String {
        val englishLevel = preferenceRepository.getPreference(
            Constants.PreferencesKey.keyEnglishLevel,
            Constants.PreferencesKey.defaultEnglishLevel
        ).first()

        val defaultInstructions = preferenceRepository.getPreference(
            Constants.PreferencesKey.keyDefaultInstructions,
            resourceProvider.getString(R.string.default_instructions)
        ).first()
        return buildString {
            if (!englishLevel.contentEquals(Constants.PreferencesKey.defaultEnglishLevel, true)) {
                appendLine(resourceProvider.getString(R.string.language_preference, englishLevel))
            }
            appendLine(defaultInstructions)
            appendLine(resourceProvider.getString(R.string.answer_limit))
        }
    }

    /**
     * 启动AI对话流程
     * 参数：
     * - openAI: OpenAI客户端实例
     * 异常：
     * - 可能抛出网络或API相关异常
     */
    private suspend fun startConversation(openAI: OpenAI) {
        var attachedMediaBase64: String? = null
        pickedImageState.value?.let { pickedMedia ->
            attachedMediaBase64 = uriConverter.toByteArray(pickedMedia.mediaUri)?.toBase64()
        }
        val temperature = preferenceRepository.getPreference(
            Constants.PreferencesKey.keyTemperature,
            Constants.PreferencesKey.TEMPERATURE_DEFAULT_VALUE
        ).first()
        val topP = preferenceRepository.getPreference(
            Constants.PreferencesKey.keyTopP,
            Constants.PreferencesKey.TOP_P_DEFAULT_VALUE
        ).first()
        val defaultInstructions: String = prepareDefaultInstructions()
        val maxCompletionTokens = preferenceRepository.getPreference(
            Constants.PreferencesKey.keyMaxCompletionTokens,
            Constants.PreferencesKey.MAX_COMPLETION_TOKENS_DEFAULT_VALUE
        ).first()
        val uniqueId = preferenceRepository.getPreference(
            Constants.PreferencesKey.keyUniqueId,
            ""
        ).first().ifEmpty { null }
        val result = openAI.chatCompletion(
            request = ChatCompletionRequest(
                model = ModelId(selectedAiModel.value),
                messages = buildList {
                    add(
                        chatMessage {
                            role = ChatRole.System
                            content {
                                text(defaultInstructions)
                            }
                        }
                    )
                    add(chatMessage {
                        role = ChatRole.User
                        content {
                            with(commandTextState) {
                                if (attachedMediaBase64 != null) {
                                    image("${Constants.IMAGE_BASE64_PREFIX}$attachedMediaBase64")
                                }
                                text(text.toString())
                            }
                        }
                    })
                },
                temperature = temperature,
                topP = topP,
                maxCompletionTokens = maxCompletionTokens,
                n = Constants.ONE,
                user = uniqueId,
                responseFormat = ChatResponseFormat.Text
            )
        )
        result.choices.firstOrNull()?.let { choice ->
            typeAnswer(choice.message.content.orEmpty())
        }
        uiModeState.value = UiMode.Answer
    }

    /**
     * 类型回答文本到答案区域
     * 参数：
     * - text: 要显示的回答文本
     */
    private fun typeAnswer(text: String) {
        answerTextState.setMarkdown(text)
    }

    /**
     * 获取要分享的文本
     * 参数：
     * - exportType: 导出类型（HTML/Markdown/PLAIN）
     * 返回值：根据导出类型生成的文本
     */
    private fun textToShare(exportType: ExportType): String {
        val selection = answerTextState.selection
        val isTextSelected = selection.start != selection.end
        val text = when (exportType) {
            ExportType.HTML -> answerTextState.toHtml()
            ExportType.MARKDOWN -> answerTextState.toMarkdown()
            ExportType.PLAIN -> answerTextState.toText()
        }
        return if (isTextSelected) {
            text.substring(selection.start, selection.end)
        } else {
            text
        }
    }

    /**
     * 触发谷歌搜索
     * 功能：使用当前答案内容进行谷歌搜索
     */
    fun onGoogleResultClicked() {
        intentResolver.googleResult(textToShare(ExportType.PLAIN))
    }

    /**
     * 分享文本
     * 功能：使用系统分享功能分享当前答案内容
     */
    fun onShareTextClicked() {
        intentResolver.shareText(
            resourceProvider.getString(R.string.title_share_via),
            textToShare(exportType.value)
        )
    }

    /**
     * 复制文本
     * 功能：将当前答案内容复制到剪贴板
     */
    fun onCopyTextClicked() {
        clipboardManager.copyFormattedTextToClipboard(
            label = resourceProvider.getString(R.string.app_name),
            plainTextToCopy = textToShare(
                if (exportType.value == ExportType.HTML) {
                    ExportType.PLAIN
                } else {
                    exportType.value
                }
            ),
            htmlTextToCopy = textToShare(ExportType.HTML)
        )
    }
    private fun splitAnkiContent(text: String): Pair<String, String> {
        val frontPattern = "正面：(.*?)背面：".toRegex()
        val frontMatch = frontPattern.find(text)
        val front = frontMatch?.groupValues?.get(1)?.trim() ?: ""
        val backPattern = "背面：(.*)".toRegex()
        val backMatch = backPattern.find(text)
        val back = backMatch?.groupValues?.get(1)?.trim() ?: text
        return Pair(front, back)
    }
    /**
     * 创建Anki卡片
     * 功能：将当前问题和答案创建为Anki卡片
     */
    fun onCreateAnkiCardClicked() {
        val front = commandTextState.text.toString()
        val back = textToShare(exportType.value)
        intentResolver.createAnkiCard(
            front,
            back
        )
    }

    /**
     * 保存为临时卡片
     * 功能：将当前问答对保存为临时卡片
     */
    fun onSaveAsCardClicked() {
        viewModelScope.launch(exceptionHandler) {
            cardRepository.insertCard(
                Card(
                    front = commandTextState.text.toString(),
                    back = answerTextState.toMarkdown()
                )
            )
            messageEventChannel.trySend(
                resourceProvider.getString(R.string.message_save_accomplished)
            )
        }
    }

    /**
     * 开始新的问答
     * 功能：重置界面开始新的对话
     */
    fun onAskAnotherQuestionClicked() {
        answerTextState.clear()
        commandTextState.clearText()
        pickedImageState.value = null
        uiModeState.value = UiMode.Ask
    }

    /**
     * 文本转语音
     * 功能：将当前答案内容转换为语音
     */
    fun onTextToSpeechClicked() {
        /**
         * 禁用语音转文本
         */
        messageEventChannel.trySend(resourceProvider.getString(R.string.error_speech_to_text_not_ready))
    }

    data class UiState(
        val headlineTitle: UiText,
        val commandTextState: TextFieldState,
        val answerTextState: RichTextState,
        val pickedMedia: PickedMedia? = null,
        val makeFullScreen: Boolean = false,
        val commandOptionsEnabled: Boolean = false,
        val commandOptions: PersistentList<PreferenceItem> = persistentListOf(),
        val uiMode: UiMode = UiMode.Ask
    )
}