package com.sun.ai.aifloat.presentation.di

import android.app.Activity
import com.sun.ai.aifloat.presentation.ui.ai.AiViewModel
import com.sun.ai.aifloat.presentation.ui.main.MainViewModel
import com.sun.ai.aifloat.presentation.util.ClipboardManager
import com.sun.ai.aifloat.presentation.util.IntentResolver
import com.sun.ai.aifloat.presentation.util.NetworkMonitor
import com.sun.ai.aifloat.presentation.util.ResourceProvider
import com.sun.ai.aifloat.presentation.util.UriConverter
import com.sun.ai.aifloat.presentation.worker.UniqueIdGeneratorWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

// Koin依赖注入模块
// 提供ViewModel、Worker等组件的注入配置
val presentationModule = lazyModule {
    factory { NetworkMonitor(androidContext()) } // 网络监控
    factory { UriConverter(androidContext()) } // URI转换器
    factory { ClipboardManager(androidContext()) } // 剪贴板管理器
    // MainViewModel注入配置
    viewModel { (activity: Activity) ->
        MainViewModel(
            get(),
            IntentResolver(activity),
            get(),
            ResourceProvider(activity)
        )
    }
    // AiViewModel注入配置
    viewModel { (activity: Activity) ->
        AiViewModel(
            get(),
            ResourceProvider(activity),
            get(),
            get(),
            IntentResolver(activity),
            get(),
            get()
        )
    }
    // 唯一ID生成工作类
    worker { UniqueIdGeneratorWorker(get(), get(), get()) }
}