package com.example.weathercompose.widget

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition

class ForecastWidget : GlanceAppWidget() {
    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            ForecastWidgetContent(preferences = currentState<Preferences>())
        }
    }

    companion object {
        val LOCATION_ID_KEY = longPreferencesKey(name = "location_id")
        val TEMPERATURE_UNIT_KEY = stringPreferencesKey(name = "temperature_unit")
    }
}