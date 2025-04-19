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
import com.example.weathercompose.ui.ui_state.DailyForecastDataUIState
import com.example.weathercompose.ui.ui_state.HourlyForecastDataUIState
import com.example.weathercompose.ui.ui_state.LocationForecastUIState
import com.example.weathercompose.ui.ui_state.LocationForecastUIState.LocationDataUIState
import com.example.weathercompose.ui.viewmodel.ForecastViewModel

private const val TAG = "ForecastCompose"

private const val COLOR_TRANSITION_ANIMATION_DURATION: Int = 700
private const val UI_ELEMENTS_COLOR = "UI elements color"

@Composable
fun ForecastContent(
    viewModel: ForecastViewModel,
    onAppearanceStateChange: (PrecipitationCondition) -> Unit,
    onNavigateToLocationSearchScreen: () -> Unit,
) {

    val locationForecastUIState by viewModel.locationForecastUIState.collectAsState()
    val precipitationCondition by viewModel.precipitationCondition.collectAsState()
    val areLocationsEmptyState by viewModel.areLocationsEmpty.collectAsState()

    LaunchedEffect(areLocationsEmptyState) {
        if (areLocationsEmptyState == true) {
            onNavigateToLocationSearchScreen()
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

    when (locationForecastUIState) {
        is LocationDataUIState -> {
            val locationDataUIState = locationForecastUIState as LocationDataUIState

            if (!locationDataUIState.isDataLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .padding(horizontal = 15.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DataLoaded(
                        locationDataUIState = locationDataUIState,
                        uiElementsBackgroundColor = animatedRowColor
                    )
                }

            } else {
                LoadingProcessIndicator()
            }
        }

        is LocationForecastUIState.ErrorForecastUIState -> TODO()
        LocationForecastUIState.NoLocationDataForecastUIState -> TODO()
        is LocationForecastUIState.InitialUIState -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {}
        }

        is LocationForecastUIState.LoadingUIState -> {
            Log.d(TAG, "current state is LocationForecastUIState.LoadingUIState")
            LoadingProcessIndicator()
        }
    }


}

@Composable
fun DataLoaded(
    locationDataUIState: LocationDataUIState,
    uiElementsBackgroundColor: Color
) {
    LocationAndWeatherInfoSection(
        locationDataUIState = locationDataUIState
    )
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp)
    )

    when (locationDataUIState.hourlyForecastsUIState) {
        is HourlyForecastDataUIState.HourlyForecastDataPresentUIState -> {
            HourlyForecastSection(
                hourlyForecasts = locationDataUIState.hourlyForecastsUIState.hourlyForecastItems,
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

    when (locationDataUIState.dailyForecastsUIState) {
        is DailyForecastDataUIState.DailyForecastDataPresentUIState -> {
            DailyForecastSection(
                dailyForecasts = locationDataUIState.dailyForecastsUIState.dailyForecastItems,
                backgroundColor = uiElementsBackgroundColor
            )
        }

        is DailyForecastDataUIState.NoActualForecastDataUIState -> {
            //TODO add text explaining no data presence
        }
    }
}

