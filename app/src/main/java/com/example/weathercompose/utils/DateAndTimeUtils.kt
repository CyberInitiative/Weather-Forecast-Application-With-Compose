package com.example.weathercompose.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

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
        ""
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
        ""
    }
}