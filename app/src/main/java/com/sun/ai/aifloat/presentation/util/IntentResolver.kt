package com.sun.ai.aifloat.presentation.util

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.sun.ai.aifloat.common.Constants

/**
 * 启动谷歌网络搜索意图并返回操作结果状态
 *
 * @param query 需要执行搜索的查询内容
 * @return Boolean 指示意图启动是否成功（true=成功启动，false=启动失败）
 */
class IntentResolver(private val context: Context) {

// 执行谷歌网络搜索
fun googleResult(query: String): Boolean {
    return launchIntent {
        // 设置意图动作为网络搜索
        action = Intent.ACTION_WEB_SEARCH
        // 将查询参数注入意图
        putExtra(SearchManager.QUERY, query)
        // 配置活动启动标志为新建任务栈
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}

    // 分享文本
    fun shareText(
        chooserTitle: String,
        textToShare: String
    ): Boolean {
        return launchChooserIntent(chooserTitle) {
            action = Intent.ACTION_SEND
            type = Constants.Intent.PLAIN_TEXT
            putExtra(Intent.EXTRA_TEXT, textToShare)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
    // 创建Anki卡片
    fun createAnkiCard(
        front: String,
        back: String
    ): Boolean {
        return launchIntent {
            action = Constants.Intent.ANKI_ACTION
            putExtra(Constants.Intent.CARD_FRONT, front)
            putExtra(Constants.Intent.CARD_BACK, back)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            `package` = Constants.Intent.ANKI_PACKAGE
        }
    }
    // 打开项目GitHub页面
    fun openProjectOnGithub() {
        launchIntent {
            action = Intent.ACTION_VIEW
            data = Constants.Intent.GITHUB_URI.toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
    // 启动意图
    private fun launchIntent(intent: Intent.() -> Unit): Boolean {
        return try {
            context.startActivity(Intent().apply(intent))
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
    // 启动选择器意图
    private fun launchChooserIntent(
        chooserTitle: String,
        intent: Intent.() -> Unit
    ): Boolean {
        return try {
            context.startActivity(Intent.createChooser(Intent().apply(intent), chooserTitle))
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}