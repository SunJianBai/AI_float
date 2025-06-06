package com.sun.ai.aifloat.presentation.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
// URI转换器
// 实现内容URI到字节数组的转换
class UriConverter(private val context: Context) {
    // 将URI内容转换为字节数组
    suspend fun toByteArray(uri: Uri) = withContext(Dispatchers.IO) {
        return@withContext kotlin.runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while (stream.read(buffer).also { length = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, length)
                }
                byteArrayOutputStream.toByteArray()
            }
        }.getOrNull()
    }
}