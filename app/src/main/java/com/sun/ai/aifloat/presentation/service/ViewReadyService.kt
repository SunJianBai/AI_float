package com.sun.ai.aifloat.presentation.service

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Display
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.sun.ai.aifloat.presentation.util.lazyFast

// 视图就绪服务基类
// 提供显示视图所需的上下文和状态管理工具
@RequiresApi(Build.VERSION_CODES.R)
abstract class ViewReadyService : LifecycleService(), SavedStateRegistryOwner, ViewModelStoreOwner {

    // 初始化保存状态注册表控制器
    private val savedStateRegistryController: SavedStateRegistryController by lazyFast {
        SavedStateRegistryController.create(this)
    }

    // 初始化视图模型存储
    private val internalViewModelStore: ViewModelStore by lazyFast {
        ViewModelStore()
    }

    // 创建覆盖视图的上下文
    internal val overlayContext: Context by lazyFast {
        // 获取默认显示屏
        val defaultDisplay: Display =
            getSystemService(DisplayManager::class.java).getDisplay(Display.DEFAULT_DISPLAY)
        // 创建显示上下文和窗口上下文
        createDisplayContext(defaultDisplay)
            .createWindowContext(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, null)
    }
    // onCreate方法中恢复保存的状态
    override fun onCreate() {
        super.onCreate()
        // 恢复保存的状态
        savedStateRegistryController.performRestore(null)
    }

    // 实现SavedStateRegistryOwner接口
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    // 实现ViewModelStoreOwner接口
    override val viewModelStore: ViewModelStore
        get() = internalViewModelStore
}