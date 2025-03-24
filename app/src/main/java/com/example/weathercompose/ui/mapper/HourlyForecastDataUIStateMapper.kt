package com.example.weathercompose.ui.mapper

import com.example.weathercompose.ui.model.HourlyForecastItem
import com.example.weathercompose.ui.ui_state.HourlyForecastDataUIState
import com.example.weathercompose.utils.getCurrentDateAndHourInTimeZone
import java.time.LocalDateTime

fun mapToHourlyForecastDataUIState(
    hourlyForecastItems: List<HourlyForecastItem>,
    timeZone: String,
): HourlyForecastDataUIState {
    val filteredData = removeNonActualData(
        hourlyForecastItems = hourlyForecastItems,
        timeZone = timeZone,
    )

    return if (filteredData.isEmpty()) {
        HourlyForecastDataUIState.NoActualForecastDataUIState
    } else {
        HourlyForecastDataUIState.HourlyForecastDataPresentUIState(
            hourlyForecastItems = filteredData,
        )
    }
}

private fun removeNonActualData(
    hourlyForecastItems: List<HourlyForecastItem>,
    timeZone: String,
): List<HourlyForecastItem> {
    val currentDateAndTime = getCurrentDateAndHourInTimeZone(timeZone = timeZone)

    return hourlyForecastItems.filter { item ->
        val itemDateAndTime = LocalDateTime.of(item.date, item.time)
        !itemDateAndTime.isBefore(currentDateAndTime.toLocalDateTime())
    }
}