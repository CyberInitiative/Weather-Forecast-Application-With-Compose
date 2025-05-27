package com.example.weathercompose.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

fun getCurrentDateAndHourInTimeZone(timeZone: String): ZonedDateTime {
    return ZonedDateTime.now(ZoneId.of(timeZone)).truncatedTo(ChronoUnit.HOURS)
}

fun getCurrentDateInTimeZone(timeZone: String): LocalDate {
    return ZonedDateTime.now(ZoneId.of(timeZone)).toLocalDate()
}

fun getCurrentHourInTimeZone(timeZone: String): LocalTime {
    return ZonedDateTime.now(ZoneId.of(timeZone)).toLocalTime().truncatedTo(ChronoUnit.HOURS)
}