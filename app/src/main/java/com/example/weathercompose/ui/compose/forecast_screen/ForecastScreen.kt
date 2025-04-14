package com.example.weathercompose.ui.compose.forecast_screen

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.weathercompose.R
import com.example.weathercompose.ui.compose.LoadingProcessIndicator
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.ui_state.CityForecastUIState
import com.example.weathercompose.ui.ui_state.CityForecastUIState.CityDataUIState
import com.example.weathercompose.ui.ui_state.DailyForecastDataUIState
import com.example.weathercompose.ui.ui_state.HourlyForecastDataUIState
import com.example.weathercompose.ui.viewmodel.ForecastViewModel

private const val TAG = "ForecastCompose"

private const val COLOR_TRANSITION_ANIMATION_DURATION: Int = 700
private const val UI_ELEMENTS_COLOR = "UI elements color"

@Composable
fun ForecastContent(
    viewModel: ForecastViewModel,
    //precipitationCondition: PrecipitationCondition,
    onAppearanceStateChange: (PrecipitationCondition) -> Unit,
    onNavigateToCitySearchScreen: () -> Unit,
) {

    val cityForecastUIState by viewModel.cityForecastUIState.collectAsState()
    val precipitationCondition by viewModel.precipitationCondition.collectAsState()
    val isCitiesEmptyState by viewModel.isCitiesEmpty.collectAsState()

    LaunchedEffect(isCitiesEmptyState) {
        if (isCitiesEmptyState == true) {
            onNavigateToCitySearchScreen()
        }
    }

    LaunchedEffect(precipitationCondition) {
        precipitationCondition?.let { onAppearanceStateChange(it) }
    }

    val targetColor = when (precipitationCondition) {
        PrecipitationCondition.NO_PRECIPITATION_DAY -> colorResource(R.color.liberty)
        PrecipitationCondition.NO_PRECIPITATION_NIGHT -> colorResource(R.color.mesmerize)
        PrecipitationCondition.PRECIPITATION_DAY -> colorResource(R.color.hilo_bay_25_percent_darker)
        PrecipitationCondition.PRECIPITATION_NIGHT -> colorResource(R.color.english_channel_10_percent_darker)
    }

    val animatedRowColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = COLOR_TRANSITION_ANIMATION_DURATION),
        label = UI_ELEMENTS_COLOR,
    )

    when (cityForecastUIState) {
        is CityDataUIState -> {
            val cityDataUIState = cityForecastUIState as CityDataUIState

            if (!cityDataUIState.isDataLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .padding(horizontal = 15.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DataLoaded(
                        cityDataUIState = cityDataUIState,
                        uiElementsBackgroundColor = animatedRowColor
                    )
                }

            } else {
                LoadingProcessIndicator()
            }
        }

        is CityForecastUIState.ErrorForecastUIState -> TODO()
        CityForecastUIState.NoCityDataForecastUIState -> TODO()
        is CityForecastUIState.InitialUIState -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {}
        }

        is CityForecastUIState.LoadingUIState -> {
            Log.d(TAG, "current state is CityForecastUIState.LoadingUIState")
            LoadingProcessIndicator()
        }
    }


}

@Composable
fun DataLoaded(
    cityDataUIState: CityDataUIState,
    uiElementsBackgroundColor: Color
) {
    CityAndWeatherInfoSection(
        cityDataUIState = cityDataUIState
    )
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp)
    )

    when (cityDataUIState.hourlyForecastsUIState) {
        is HourlyForecastDataUIState.HourlyForecastDataPresentUIState -> {
            HourlyForecastSection(
                hourlyForecasts = cityDataUIState.hourlyForecastsUIState.hourlyForecastItems,
                backgroundColor = uiElementsBackgroundColor
            )
        }

        is HourlyForecastDataUIState.NoActualForecastDataUIState -> {
            //TODO add text explaining no data presence
        }
    }

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    )

    when (cityDataUIState.dailyForecastsUIState) {
        is DailyForecastDataUIState.DailyForecastDataPresentUIState -> {
            DailyForecastSection(
                dailyForecasts = cityDataUIState.dailyForecastsUIState.dailyForecastItems,
                backgroundColor = uiElementsBackgroundColor
            )
        }

        is DailyForecastDataUIState.NoActualForecastDataUIState -> {
            //TODO add text explaining no data presence
        }
    }
}

