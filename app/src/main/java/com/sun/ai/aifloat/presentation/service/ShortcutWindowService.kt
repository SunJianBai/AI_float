package com.sun.ai.aifloat.presentation.service

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ServiceCompat
import com.sun.ai.aifloat.presentation.ui.ai.AiActivity
import com.sun.ai.aifloat.presentation.ui.core.component.AppLogo
import com.sun.ai.aifloat.presentation.ui.core.theme.appLogoSizeForOverlayService
import com.sun.ai.aifloat.presentation.util.NotificationsHelper
import com.sun.ai.aifloat.presentation.util.isSdk34OrUp

// 快捷方式窗口服务
// 显示悬浮窗并处理点击事件跳转到AI主界面
@RequiresApi(Build.VERSION_CODES.R)
class ShortcutWindowService : ComposeOverlayViewService() {
    private val overlayVisible = mutableStateOf(true)

    // 启动服务
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        start(startId)
        return super.onStartCommand(intent, flags, startId)
    }

    // 启动前台服务
    private fun start(startId: Int) {
        NotificationsHelper.createNotificationChannel(this)
        ServiceCompat.startForeground(
            this,
            startId,
            NotificationsHelper.buildWindowServiceNotification(this),
            if (isSdk34OrUp) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0
            }
        )
    }

    // 实现父类的Content方法
    @RequiresApi(Build.VERSION_CODES.R)
    @Composable
    override fun Content() {
        OverlayDraggableContainer {
            val overlayVisible by remember { this@ShortcutWindowService.overlayVisible }
            AnimatedVisibility(overlayVisible) {
                AppLogo( // 应用LOGO组件
                    modifier = Modifier.size(appLogoSizeForOverlayService),
                    onClick = this@ShortcutWindowService::onOverlayBubbleClicked
                )
            }
        }
    }

    // 悬浮窗点击事件处理
    private fun onOverlayBubbleClicked() {
        startActivity(Intent(this, AiActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}