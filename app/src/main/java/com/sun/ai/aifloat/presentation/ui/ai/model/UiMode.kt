package com.sun.ai.aifloat.presentation.ui.ai.model

/**
 * UI模式密封类
 * 功能：
 * - 定义AI对话界面的不同显示模式
 * - 支持状态驱动的UI更新
 * 技术栈：
 * - 使用Kotlin密封类实现类型安全的状态管理
 * - 与Jetpack Compose的when表达式完美配合
 */
sealed class UiMode {
    /**
     * 提问模式：用户输入查询内容
     * 功能：显示输入框供用户输入问题
     */
    object Ask : UiMode()
    
    /**
     * 回答模式：显示AI返回结果
     * 功能：展示AI生成的回答内容
     */
    object Answer : UiMode()
    
    /**
     * 加载模式：显示进度指示器
     * 功能：在等待AI响应时显示加载动画
     */
    object Loading : UiMode()
    
    /**
     * 错误模式：显示错误信息
     * 功能：当发生错误时显示错误提示
     */
    object Error : UiMode()
}