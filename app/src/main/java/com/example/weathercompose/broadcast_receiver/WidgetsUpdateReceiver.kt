package com.example.weathercompose.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.updateAll
import com.example.weathercompose.utils.scheduleWidgetsUpdate
import com.example.weathercompose.widget.ForecastWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class WidgetsUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) = doAsync(
        appScope = CoroutineScope(Dispatchers.IO)
    ) {
        if (p0 != null) {
            ForecastWidget().updateAll(context = p0)
            scheduleWidgetsUpdate(context = p0)
        }
    }
}

fun BroadcastReceiver.doAsync(
    appScope: CoroutineScope,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) {
    val pendingResult = goAsync()
    appScope.launch(coroutineContext) {
        try {
            block()
        } finally {
            pendingResult.finish()
        }
    }
}