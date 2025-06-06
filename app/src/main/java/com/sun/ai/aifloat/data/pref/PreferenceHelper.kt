package com.sun.ai.aifloat.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
// SharedPreferences工具类
// 使用DataStore进行偏好存储
class PreferenceHelper(private val dataStore: DataStore<Preferences>) {
    // 获取偏好值
    fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T):
            Flow<T> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        val result = preferences[key] ?: defaultValue
        result
    }
    // 获取所有偏好
    fun getAllPreferences() = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences.asMap()
    }

    // 存储偏好值
    suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
    // 移除偏好
    suspend fun <T> removePreference(key: Preferences.Key<T>) {
        dataStore.edit {
            it.remove(key)
        }
    }
    // 清空所有偏好
    suspend fun clearAllPreference() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}