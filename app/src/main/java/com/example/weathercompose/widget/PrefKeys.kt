package com.example.weathercompose.widget

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PrefKeys {
    val LOCATION_ID_KEY = longPreferencesKey(name = "location_id")
    val TEMPERATURE_UNIT_KEY = stringPreferencesKey(name = "temperature_unit")
}