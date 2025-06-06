package com.sun.ai.aifloat.presentation.service

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.sun.ai.aifloat.presentation.util.lazyFast
import kotlin.math.roundToInt

// Jetpack Compose实现的悬浮窗服务基类
// 提供Compose UI的悬浮窗实现基础功能
@RequiresApi(Build.VERSION_CODES.R)
abstract class ComposeOverlayViewService : ViewReadyService() {

    // 构建悬浮窗布局参数
    private val layoutParams by lazyFast {
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.END // 设置重力为垂直居中和右侧对齐
        }
    }

    // 当前悬浮窗的位置偏移量
    private var overlayOffset by mutableStateOf(Offset.Zero)

    // 窗口管理器
    private val windowManager by lazyFast {
        overlayContext.getSystemService(WindowManager::class.java)
    }

    // Compose视图
    private val composeView by lazyFast {
        ComposeView(overlayContext)
    }
    // onBind方法处理绑定意图
    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
    // onCreate方法初始化Compose视图
    override fun onCreate() {
        super.onCreate()

        // 绑定Compose生命周期、视图模型和保存状态到视图服务
        composeView.setViewTreeLifecycleOwner(this)
        composeView.setViewTreeViewModelStoreOwner(this)
        composeView.setViewTreeSavedStateRegistryOwner(this)

        // 设置Compose视图内容
        composeView.setContent { Content() }

        // 将Compose视图添加到窗口管理器
        windowManager.addView(composeView, layoutParams)
    }

    // onDestroy方法清理资源
    override fun onDestroy() {
        super.onDestroy()
        // 从窗口管理器移除Compose视图
        windowManager.removeView(composeView)
    }

    // 抽象方法，由子类实现具体内容
    @Composable
    abstract fun Content()

    // 可拖动的悬浮窗容器（非默认启用）
    @Composable
    internal fun OverlayDraggableContainer(
        modifier: Modifier = Modifier,
        content: @Composable BoxScope.() -> Unit
    ) =
        Box(
            modifier = modifier.pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()

                    // 更新当前偏移量并调整窗口位置
                    val newOffset = overlayOffset + dragAmount
                    overlayOffset = newOffset

                    layoutParams.apply {
                        x = overlayOffset.x.roundToInt()
                        y = overlayOffset.y.roundToInt()
                    }
                    windowManager.updateViewLayout(composeView, layoutParams)
                }
            },
            content = content
        )
}