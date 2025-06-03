package com.sun.ai.aifloat.presentation

import android.app.Application
import com.sun.ai.aifloat.data.di.dataModule
import com.sun.ai.aifloat.presentation.di.presentationModule
import com.sun.ai.aifloat.presentation.worker.UniqueIdGeneratorWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.lazyModules
import org.koin.dsl.KoinAppDeclaration

@OptIn(KoinExperimentalAPI::class)
class ApplicationLoader : Application(), KoinStartup {
    override fun onCreate() {
        super.onCreate()
        UniqueIdGeneratorWorker.enqueueWorker(this)
    }

    override fun onKoinStartup(): KoinAppDeclaration = {
        androidContext(this@ApplicationLoader)
        workManagerFactory()
        lazyModules(dataModule, presentationModule)
    }
}