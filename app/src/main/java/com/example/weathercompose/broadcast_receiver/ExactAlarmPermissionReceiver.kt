package com.example.weathercompose.broadcast_receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.weathercompose.utils.scheduleWidgetsUpdate

class ExactAlarmPermissionReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED) {
            if (p0 != null) {
                scheduleWidgetsUpdate(context = p0)
            }
        }
    }
}