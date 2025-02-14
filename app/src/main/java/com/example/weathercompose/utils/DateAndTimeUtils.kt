package com.example.weathercompose.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private const val TAG = "DateAndTimeUtils"

private const val DATE_FORMAT = "yyyy-MM-dd"
private const val TIME_FORMAT = "HH:mm"

fun dateStringToDayOfWeek(date: String): String {
//    if (!date.matches(DATE_FORMAT.toRegex())){
//        throw IllegalArgumentException("Date string does not match DATE_FORMAT pattern!")
//    }
    Log.d(TAG, "dateStringToDayOfWeek() called; Date is $date")
    val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    val outputFormat = SimpleDateFormat("EE", Locale.getDefault())

    val parsedDate = inputFormat.parse(date)

    return if (parsedDate != null) {
        outputFormat.format(parsedDate)
    } else {
        "--"
    }
}

fun dateStringToMonthAndDayNumber(date: String): String {
//    if (!date.matches(DATE_FORMAT.toRegex())){
//        throw IllegalArgumentException("Date string does not match DATE_FORMAT pattern!")
//    }
    Log.d(TAG, "dateStringToMonthAndDayNumber() called; Date is $date")
    val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    val parsedDate = inputFormat.parse(date)

    return if (parsedDate != null) {
        outputFormat.format(parsedDate)
    } else {
        "--"
    }
}

fun timeToHour(time: String): String {
    val inputFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
    val outputFormat = SimpleDateFormat("HH", Locale.getDefault())

    val hour = inputFormat.parse(time)

    return if (hour != null) {
        outputFormat.format(hour)
    } else {
        "--"
    }
}

fun getCurrentHourForTimeZone(cityTimeZone: String): String {
    val timeZone = TimeZone.getTimeZone(cityTimeZone)
    val calendar = Calendar.getInstance(timeZone)
    val dateFormat = SimpleDateFormat("HH")
    dateFormat.timeZone = timeZone
    return dateFormat.format(calendar.time)
}