package com.example.weathercompose.broadcast_receiver

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.weathercompose.utils.cancelWidgetsUpdate
import com.example.weathercompose.utils.scheduleWidgetsUpdate
import com.example.weathercompose.widget.ForecastWidget

class ForecastWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ForecastWidget()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        if (context != null) {
            scheduleWidgetsUpdate(context = context)
        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        if(context != null){
            cancelWidgetsUpdate(context = context)
        }
    }
}