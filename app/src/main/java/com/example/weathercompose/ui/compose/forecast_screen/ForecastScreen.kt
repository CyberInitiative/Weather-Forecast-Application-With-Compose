package com.example.weathercompose.ui.compose.forecast_screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.weathercompose.R
import com.example.weathercompose.ui.compose.LoadingProcessIndicator
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.ui_state.LocationUIState
import com.example.weathercompose.ui.viewmodel.ForecastViewModel

private const val TAG = "ForecastCompose"
private const val COLOR_TRANSITION_ANIMATION_DURATION: Int = 700

@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel,
    locationId: Long?,
    onAppearanceStateChange: (PrecipitationCondition) -> Unit,
    onNavigateToLocationSearchScreen: () -> Unit,
) {

    val precipitationCondition by viewModel.precipitationCondition.collectAsState()
    val locationsUIStates by viewModel.locationsUIStates.collectAsState()

    LaunchedEffect(precipitationCondition) {
        onAppearanceStateChange(precipitationCondition)
    }

    LaunchedEffect(locationsUIStates) {
        if (locationsUIStates?.isEmpty() == true) {
            onNavigateToLocationSearchScreen()
        }
    }

    val uiElementsBackgroundColor by animateColorAsState(
        targetValue = when (precipitationCondition) {
            PrecipitationCondition.NO_PRECIPITATION_DAY -> colorResource(R.color.liberty)
            PrecipitationCondition.NO_PRECIPITATION_NIGHT -> colorResource(R.color.mesmerize)
            PrecipitationCondition.PRECIPITATION_DAY -> {
                colorResource(R.color.hilo_bay_25_percent_darker)
            }

            PrecipitationCondition.PRECIPITATION_NIGHT -> {
                colorResource(R.color.english_channel_10_percent_darker)
            }
        },
        animationSpec = tween(durationMillis = COLOR_TRANSITION_ANIMATION_DURATION),
    )

    val pagerState = rememberPagerState(pageCount = { locationsUIStates?.size ?: 0 })

    LaunchedEffect(locationId) {
        if (locationId != null) {
            val index = locationsUIStates?.indexOfFirst { it.id == locationId } ?: 0
            pagerState.scrollToPage(if (index != -1) index else 0)
        }
    }

    LaunchedEffect(pagerState, locationsUIStates) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.onPageSelected(page)
        }
    }

    ForecastContent(
        locationsUIStates = locationsUIStates,
        uiElementsBackgroundColor = uiElementsBackgroundColor,
        pagerState = pagerState,
    )
}

@Composable
private fun ForecastContent(
    locationsUIStates: List<LocationUIState>?,
    uiElementsBackgroundColor: Color,
    pagerState: PagerState,
) {
    if (locationsUIStates != null) {

        LoadedData(
            locationsUIStates = locationsUIStates,
            uiElementsBackgroundColor = uiElementsBackgroundColor,
            pagerState = pagerState,
        )
    } else {
        LoadingProcessIndicator()
    }
}

@Composable
private fun LoadedData(
    locationsUIStates: List<LocationUIState>,
    uiElementsBackgroundColor: Color,
    pagerState: PagerState,
) {
    ConstraintLayout {
        val (pager, pageIndicator) = createRefs()

        val pagerIndicatorModifier = Modifier.constrainAs(pageIndicator) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }

        val pagerModifier = Modifier.constrainAs(pager) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(pageIndicator.top)
        }

        HorizontalPager(
            state = pagerState,
            modifier = pagerModifier
        ) { page ->
            val currentLocation = locationsUIStates[page]

            LocationPage(
                currentLocation = currentLocation,
                uiElementsBackgroundColor = uiElementsBackgroundColor,
            )
        }

        PageIndicator(
            modifier = pagerIndicatorModifier,
            pagerState = pagerState,
            uiElementsBackgroundColor = uiElementsBackgroundColor,
        )
    }
}

@Composable
private fun LocationPage(
    currentLocation: LocationUIState,
    uiElementsBackgroundColor: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .padding(horizontal = 15.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (currentLocation.isLoading) {
            LoadingProcessIndicator()
        } else {
            LocationAndWeatherInfoSection(
                locationUIState = currentLocation
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp)
            )

            HourlyForecastSection(
                hourlyForecasts = currentLocation.hourlyForecasts,
                backgroundColor = uiElementsBackgroundColor
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )

            DailyForecastSection(
                dailyForecasts = currentLocation.dailyForecasts,
                backgroundColor = uiElementsBackgroundColor
            )
        }
    }
}

@Composable
private fun PageIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    uiElementsBackgroundColor: Color,
) {
    Row(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 10.dp, top = 5.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color = if (pagerState.currentPage == iteration) {
                Color.White
            } else {
                uiElementsBackgroundColor
            }

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(7.dp)
            )
        }
    }
}