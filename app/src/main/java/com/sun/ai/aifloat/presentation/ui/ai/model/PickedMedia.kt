package com.sun.ai.aifloat.presentation.ui.ai.model

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Immutable
import java.util.*

@Immutable
/**
 * 已选媒体数据类
 * 功能：
 * - 管理用户选择的媒体文件（图片/视频）
 * - 封装媒体URI和预览信息
 * 技术栈：
 * - 使用Android的Uri类处理媒体路径
 * - 采用Kotlin数据类实现不可变状态
 */
data class PickedMedia(
    // 媒体文件的URI
    val mediaUri: Uri,
    // 预览文本（可空）
    val previewText: String? = null
) {
    // 获取媒体文件的唯一标识符
    fun getMediaId(): String {
        return mediaUri.lastPathSegment ?: randomStringUUID()
    }
}

// 扩展函数：将PickedMedia转换为字符串表示
fun PickedMedia?.toString(context: Context): String {
    return this?.let { "PickedMedia(${it.mediaUri})" } ?: "No media picked"
}