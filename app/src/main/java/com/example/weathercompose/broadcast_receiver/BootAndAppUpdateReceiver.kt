package com.example.weathercompose.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.weathercompose.utils.scheduleWidgetsUpdate

class BootAndAppUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p0 != null && p1 != null) {
            if (p1.action == Intent.ACTION_BOOT_COMPLETED ||
                p1.action == Intent.ACTION_MY_PACKAGE_REPLACED
            ) {
                scheduleWidgetsUpdate(context = p0)
            }
        }
    }
}