package com.sun.ai.aifloat.presentation.service

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.eventFlow
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
// 悬浮窗服务管理器
// 处理多个悬浮窗的生命周期
class OverlayServiceManager(
    activity: ComponentActivity,
    overlayServiceAllowed: Flow<Boolean>
) {
    init {
        combine(
            overlayServiceAllowed,
            activity.lifecycle.eventFlow
        ) { allowed, lifecycleState ->
            when (lifecycleState) {
                Lifecycle.Event.ON_RESUME -> {
                    activity.stopService(Intent(activity, ShortcutWindowService::class.java)) // 暂停时停止服务
                }

                Lifecycle.Event.ON_PAUSE -> {
                    if (allowed) {
                        ContextCompat.startForegroundService( // 恢复时启动前台服务
                            activity,
                            Intent(activity, ShortcutWindowService::class.java)
                        )
                    }
                }

                else -> return@combine
            }
        }.launchIn(activity.lifecycleScope)
    }
}