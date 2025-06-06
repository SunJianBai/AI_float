// 创建新的AiService接口
package com.sun.ai.aifloat.domain.usecase

import kotlinx.coroutines.flow.Flow

/**
 * AI服务接口，定义AI相关功能的基本操作
 */
interface AiService {
    /**
     * 调用AI API进行对话
     * @param apiKey API密钥
     * @param host 主机地址
     * @param systemPrompt 系统提示词
     * @param userQuery 用户查询内容
     * @param temperature 温度参数
     * @param topP Top-P值
     * @param timeout 超时时间
     * @return 返回AI响应结果
     */
    suspend fun callAiApi(
        apiKey: String,
        host: String,
        systemPrompt: String,
        userQuery: String,
        temperature: Float,
        topP: Float,
        timeout: Long
    ): String

    /**
     * 获取初始问候语
     * @return 返回AI的初始问候语
     */
    suspend fun getInitialGreeting(): String

    /**
     * 取消当前请求
     */
    fun cancelCurrentRequest()
}