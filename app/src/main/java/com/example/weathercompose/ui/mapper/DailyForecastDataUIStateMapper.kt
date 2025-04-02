package com.example.weathercompose.ui.mapper

import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.ui_state.DailyForecastDataUIState

fun mapToDailyForecastDataUIState(
    dailyForecastItems: List<DailyForecastItem>,
    timeZone: String,
): DailyForecastDataUIState {

//    val filteredData = removeNonActualData(
//        dailyForecastItems = dailyForecastItems,
//        timeZone = timeZone,
//    )

    return DailyForecastDataUIState.DailyForecastDataPresentUIState(
        dailyForecastItems = dailyForecastItems,
    )

//    return if (filteredData.isEmpty()) {
//        DailyForecastDataUIState.NoActualForecastDataUIState
//    } else {
//        DailyForecastDataUIState.DailyForecastDataPresentUIState(
//            dailyForecastItems = dailyForecastItems,
//        )
//    }
}

//private fun removeNonActualData(
//    dailyForecastItems: List<DailyForecastItem>,
//    timeZone: String,
//): List<DailyForecastItem> {
//    val currentDate = getCurrentDateInTimeZone(timeZone = timeZone)
//
//    return dailyForecastItems.filter { item ->
//        !item.date.isBefore(currentDate)
//    }
//}