package com.example.weathercompose.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.weathercompose.utils.scheduleWidgetsUpdate

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_BOOT_COMPLETED) {
            if (p0 != null) {
                scheduleWidgetsUpdate(context = p0)
            }
        }
    }
}