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

val presentationModule = lazyModule {
    factory { NetworkMonitor(androidContext()) }
    factory { UriConverter(androidContext()) }
    factory { ClipboardManager(androidContext()) }
    viewModel { (activity: Activity) ->
        MainViewModel(
            get(),
            IntentResolver(activity),
            get(),
            ResourceProvider(activity)
        )
    }
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
    worker { UniqueIdGeneratorWorker(get(), get(), get()) }
}