package com.sun.ai.aifloat.data

import androidx.datastore.preferences.core.Preferences
import com.sun.ai.aifloat.data.pref.PreferenceHelper
import com.sun.ai.aifloat.domain.PreferenceRepository
import kotlinx.coroutines.flow.Flow

class PreferenceRepositoryImpl(
    private val preferenceHelper: PreferenceHelper
) : PreferenceRepository {
    override fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        preferenceHelper.getPreference(key, defaultValue)

    override fun getAllPreferences(): Flow<Map<Preferences.Key<*>, Any>> =
        preferenceHelper.getAllPreferences()

    override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) =
        preferenceHelper.putPreference(key, value)

    override suspend fun <T> removePreference(key: Preferences.Key<T>) =
        preferenceHelper.removePreference(key)

    override suspend fun clearAllPreference() = preferenceHelper.clearAllPreference()
}