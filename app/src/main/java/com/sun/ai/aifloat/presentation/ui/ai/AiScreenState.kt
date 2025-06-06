package com.sun.ai.aifloat.presentation.ui.ai

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Stable

/**
 * AI对话屏幕状态类
 * 功能：
 * - 管理AI对话界面的核心状态
 * - 支持响应式更新
 * 技术栈：
 * - 使用Jetpack Compose的稳定状态管理（@Stable）
 * - 采用不可变数据结构（@Immutable）
 * 数据结构说明：
 * - messages: 对话历史消息列表
 * - currentQuery: 当前查询内容
 * - isLoading: 加载状态
 * - errorMessage: 错误信息
 * - selectedImageUri: 已选图片的URI
 * - themeMode: 主题模式（系统/亮色/暗色）
 */
@Stable
class AiScreenState(
    val messages: List<Message> = emptyList(),
    val currentQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedImageUri: Uri? = null,
    val themeMode: ThemeMode = ThemeMode.System
) {
    companion object {
        // 创建初始状态实例
        fun initial() = AiScreenState()
    }
}

/**
 * 消息密封类
 * 功能：
 * - 定义不同类型的消息类型
 * - 实现多态消息处理
 */
@Immutable
sealed class Message {
    /**
     * 用户发送的消息
     * 参数：
     * - text: 消息文本内容
     */
    data class UserMessage(val text: String) : Message()
    
    /**
     * AI回复的消息
     * 参数：
     * - text: 回复文本内容
     */
    data class AiResponse(val text: String) : Message()
    
    /**
     * 错误消息
     * 参数：
     * - error: 错误描述信息
     */
    data class ErrorMessage(val error: String) : Message()
}

/**
 * 主题模式枚举类
 * 功能：
 * - 管理应用的主题显示模式
 * 枚举值：
 * - System: 系统默认主题
 * - Light: 亮色主题
 * - Dark: 暗色主题
 */
enum class ThemeMode {
    System, // 系统主题
    Light,  // 亮色主题
    Dark    // 暗色主题
}