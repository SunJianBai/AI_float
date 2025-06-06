package com.sun.ai.aifloat.presentation.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
// 剪贴板管理类
class ClipboardManager(context: Context) {
    private val clipboardManager by lazyFast {
        ContextCompat.getSystemService(context, ClipboardManager::class.java)
    }

    fun copyTextToClipboard(   // 复制纯文本
        label: String,
        textToCopy: String
    ) {
        clipboardManager?.setPrimaryClip(ClipData.newPlainText(label, textToCopy))
    }

    fun copyFormattedTextToClipboard( // 复制带格式文本（含HTML）
        label: String,
        plainTextToCopy: String,
        htmlTextToCopy: String
    ) {
        clipboardManager?.setPrimaryClip(
            ClipData.newHtmlText(
                label,
                plainTextToCopy,
                htmlTextToCopy
            )
        )
    }
}