package com.example.weathercompose.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.weathercompose.data.model.forecast.TemperatureUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFERENCES_NAME = "appSettings"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)
val TEMPERATURE_UNIT_KEY = stringPreferencesKey("temperature_unit")

class AppSettings(private val context: Context) {
    val currentTemperatureUnit: Flow<TemperatureUnit> = context.dataStore.data.map { preferences ->
        preferences[TEMPERATURE_UNIT_KEY]?.let {
            TemperatureUnit.entries.find { unit -> unit.name == it } ?: TemperatureUnit.CELSIUS
        } ?: TemperatureUnit.CELSIUS
    }

    suspend fun setCurrentTemperatureUnit(temperatureUnit: TemperatureUnit) {
        context.dataStore.edit { appSettings ->
            appSettings[TEMPERATURE_UNIT_KEY] = temperatureUnit.name
        }
    }
}