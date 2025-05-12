package com.example.weathercompose.data.model

enum class ForecastUpdateFrequency(val value: Int) {
    ONE_HOUR(1),
    TWO_HOURS(2),
    THREE_HOURS(3),
    SIX_HOURS(6),
    TWELVE_HOURS(12),
    TWENTY_FOUR_HOURS(24)
}