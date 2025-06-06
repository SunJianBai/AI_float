package com.sun.ai.aifloat.data

import androidx.datastore.preferences.core.Preferences
import com.sun.ai.aifloat.data.pref.PreferenceHelper
import com.sun.ai.aifloat.domain.PreferenceRepository
import kotlinx.coroutines.flow.Flow

// 偏好设置接口
// 抽象化配置管理方法
class PreferenceRepositoryImpl(
    private val preferenceHelper: PreferenceHelper
) : PreferenceRepository {
    override fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        preferenceHelper.getPreference(key, defaultValue)  // 获取偏好值

    override fun getAllPreferences(): Flow<Map<Preferences.Key<*>, Any>> =
        preferenceHelper.getAllPreferences() // 获取所有偏好值

    override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) =
        preferenceHelper.putPreference(key, value)  // 存储偏好值

    override suspend fun <T> removePreference(key: Preferences.Key<T>) =
        preferenceHelper.removePreference(key) // 移除特定偏好

    override suspend fun clearAllPreference() = preferenceHelper.clearAllPreference() // 清空所有偏好
}