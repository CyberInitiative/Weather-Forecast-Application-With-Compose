package com.example.weathercompose.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.weathercompose.broadcast_receiver.WidgetsUpdateReceiver
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

private const val REQUEST_CODE = 0

fun scheduleWidgetsUpdate(
    context: Context,
) {
    val intent = Intent(context, WidgetsUpdateReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val triggerTime = getCurrentDateWithNextHourInMillis()

    when (canScheduleExactAlarms(alarmManager = alarmManager)) {
        true -> {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC,
                triggerTime,
                pendingIntent
            )
        }

        false -> {
            alarmManager.setRepeating(
                AlarmManager.RTC,
                triggerTime,
                AlarmManager.INTERVAL_HOUR,
                pendingIntent,
            )
        }
    }
}

fun cancelWidgetsUpdate(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, WidgetsUpdateReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}


private fun getCurrentDateWithNextHourInMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    val currentDateAndTimeWithNextHour = LocalDateTime
        .now()
        .truncatedTo(ChronoUnit.HOURS)
        .plusHours(1)
    return currentDateAndTimeWithNextHour
        .atZone(zoneId)
        .toInstant()
        .toEpochMilli()
}

fun canScheduleExactAlarms(context: Context): Boolean {
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    return canScheduleExactAlarms(alarmManager = alarmManager)
}

fun canScheduleExactAlarms(alarmManager: AlarmManager): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}