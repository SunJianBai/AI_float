// 创建新的DefaultAiService类
package com.sun.ai.aifloat.domain.usecase

import kotlinx.coroutines.flow.Flow

/**
 * AI服务接口的默认实现
 */
class DefaultAiService : AiService {
    override suspend fun callAiApi(
        apiKey: String,
        host: String,
        systemPrompt: String,
        userQuery: String,
        temperature: Float,
        topP: Float,
        timeout: Long
    ): String {
        // 实现基本的AI调用逻辑
        return "Mock response for: $userQuery"
    }

    override suspend fun getInitialGreeting(): String {
        // 返回初始问候语
        return "您好！我是您的AI助手，有什么可以帮助您的吗？"
    }

    override fun cancelCurrentRequest() {
        // 实现请求取消逻辑
    }
}