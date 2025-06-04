package com.example.weathercompose.widget

import android.content.Context
import androidx.glance.appwidget.updateAll

class WidgetUpdateManager(private val context: Context) {

    suspend fun updateAll() {
        ForecastWidget().updateAll(context = context)
    }
}