package com.sun.ai.aifloat.presentation.util

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID
// Kotlin语法糖扩展
// 非线程安全的延迟初始化
fun <T> lazyFast(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)
// 判断当前是否白天
fun isDaytime(timeZone: ZoneId = ZoneId.systemDefault()): Boolean {
    val now = LocalTime.now(timeZone)
    val startOfDay = LocalTime.of(6, 0)  // 6:00 AM
    val endOfDay = LocalTime.of(18, 0)   // 6:00 PM
    return now.isAfter(startOfDay) && now.isBefore(endOfDay)
}
// 生成随机UUID
fun randomStringUUID() = UUID.randomUUID().toString()
// 字节数组转Base64字符串
suspend fun ByteArray.toBase64(): String = withContext(Dispatchers.IO) {
    return@withContext Base64.encodeToString(this@toBase64, Base64.NO_WRAP)
}