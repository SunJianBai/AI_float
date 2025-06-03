package com.sun.ai.aifloat.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.sun.ai.aifloat.common.Constants
import com.sun.ai.aifloat.data.CardRepositoryImpl
import com.sun.ai.aifloat.data.PreferenceRepositoryImpl
import com.sun.ai.aifloat.data.db.AppDatabase
import com.sun.ai.aifloat.data.pref.PreferenceHelper
import com.sun.ai.aifloat.domain.CardRepository
import com.sun.ai.aifloat.domain.PreferenceRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.lazyModule

val Context.dataStore by preferencesDataStore(name = "ai_dict")

val dataModule = lazyModule {
    single { provideDataStore(get()) }
    single { PreferenceHelper(get()) }
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            Constants.Database.DB_NAME
        ).build()
    }
    single { get<AppDatabase>().cardDAO() }
    factory { PreferenceRepositoryImpl(get()) }
    factory { CardRepositoryImpl(get()) }
    factoryOf(::CardRepositoryImpl) { bind<CardRepository>() }
    factoryOf(::PreferenceRepositoryImpl) { bind<PreferenceRepository>() }
}

fun provideDataStore(context: Context): DataStore<Preferences> {
    return context.dataStore
}