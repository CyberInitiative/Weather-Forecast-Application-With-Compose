package com.example.weathercompose.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.weathercompose.data.model.ForecastUpdateFrequency
import com.example.weathercompose.data.model.forecast.TemperatureUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFERENCES_NAME = "appSettings"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

val TEMPERATURE_UNIT_KEY = stringPreferencesKey("temperature_unit")
val FORECAST_UPDATE_FREQUENCY_KEY = intPreferencesKey("forecast_update_frequency")
val LAST_TIME_FORECASTS_UPDATED_KEY = longPreferencesKey("last_time_forecasts_updated")

class AppSettings(private val context: Context) {
    val currentTemperatureUnit: Flow<TemperatureUnit> = context.dataStore.data.map { preferences ->
        preferences[TEMPERATURE_UNIT_KEY]?.let {
            TemperatureUnit.entries.find { unit -> unit.name == it } ?: TemperatureUnit.CELSIUS
        } ?: TemperatureUnit.CELSIUS
    }
    val forecastUpdateFrequency: Flow<ForecastUpdateFrequency> =
        context.dataStore.data.map { preferences ->
            preferences[FORECAST_UPDATE_FREQUENCY_KEY]?.let {
                ForecastUpdateFrequency.entries.find { frequency -> frequency.value == it }
            } ?: ForecastUpdateFrequency.ONE_HOUR
        }

    val lastTimeForecastsUpdated: Flow<Long> =
        context.dataStore.data.map { preferences ->
            preferences[LAST_TIME_FORECASTS_UPDATED_KEY] ?: 0
        }

    suspend fun setCurrentTemperatureUnit(temperatureUnit: TemperatureUnit) {
        context.dataStore.edit { appSettings ->
            appSettings[TEMPERATURE_UNIT_KEY] = temperatureUnit.name
        }
    }

    suspend fun setForecastUpdateFrequency(forecastUpdateFrequency: ForecastUpdateFrequency) {
        context.dataStore.edit { appSettings ->
            appSettings[FORECAST_UPDATE_FREQUENCY_KEY] = forecastUpdateFrequency.value
        }
    }

    suspend fun setLastTimeForecastsUpdated(lastTimeForecastsUpdated: Long) {
        context.dataStore.edit { appSettings ->
            appSettings[LAST_TIME_FORECASTS_UPDATED_KEY] = lastTimeForecastsUpdated
        }
    }
}