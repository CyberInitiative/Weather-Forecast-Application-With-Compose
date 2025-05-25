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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.weathercompose.ui.compose.LoadingProcessIndicator
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
import com.example.weathercompose.ui.theme.HiloBay25PerDarker
import com.example.weathercompose.ui.theme.Liberty
import com.example.weathercompose.ui.theme.MediumDarkShadeCyanBlue
import com.example.weathercompose.ui.theme.Solitaire5PerDarker
import com.example.weathercompose.ui.ui_state.LocationUIState
import com.example.weathercompose.ui.viewmodel.ForecastViewModel

private const val TAG = "ForecastCompose"
private const val COLOR_TRANSITION_ANIMATION_DURATION: Int = 700

@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel,
    onAppearanceStateChange: (WeatherAndDayTimeState) -> Unit,
    onNavigateToLocationSearchScreen: () -> Unit,
    locationId: Long?
) {
    val precipitationCondition by viewModel.weatherAndDayTimeState.collectAsState()
    val locationsUIStates by viewModel.locationsUIStates.collectAsState()

    LaunchedEffect(precipitationCondition) {
        onAppearanceStateChange(precipitationCondition)
    }

    LaunchedEffect(locationsUIStates) {
        if (locationsUIStates?.isEmpty() == true) {
            onNavigateToLocationSearchScreen()
        }
    }

    val uiElementsColor by animateColorAsState(
        targetValue = when (precipitationCondition) {
            WeatherAndDayTimeState.NO_PRECIPITATION_DAY -> Liberty
            WeatherAndDayTimeState.NO_PRECIPITATION_NIGHT -> MediumDarkShadeCyanBlue
            WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_DAY -> HiloBay25PerDarker
            WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_NIGHT -> Solitaire5PerDarker
        },
        animationSpec = tween(durationMillis = COLOR_TRANSITION_ANIMATION_DURATION),
    )

    val pagerState = rememberPagerState(pageCount = { locationsUIStates?.size ?: 0 })

    LaunchedEffect(locationId) {
        /*val index = if (locationId != null) {
            Log.d(TAG, "IF; locationId != null; locationId: $locationId")
            locationsUIStates?.indexOfFirst { it.id == locationId } ?: 0
        } else {
            Log.d(
                TAG,
                "ELSE; locationId == null; viewModel.currentLocationId: ${viewModel.currentLocationId}"
            )
            locationsUIStates?.indexOfFirst { it.id == viewModel.currentLocationId } ?: 0
        }*/

        val currentPage = pagerState.currentPage
        val index = locationsUIStates?.indexOfFirst { it.id == locationId } ?: currentPage
        pagerState.scrollToPage(if (index != -1) index else currentPage)
    }

    LaunchedEffect(pagerState, locationsUIStates) {
        if (locationsUIStates?.isNotEmpty() == true) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                viewModel.onPageSelected(page)
            }
        }
    }

    ForecastContent(
        locationsUIStates = locationsUIStates,
        uiElementsColor = uiElementsColor,
        pagerState = pagerState,
    )
}

@Composable
private fun ForecastContent(
    locationsUIStates: List<LocationUIState>?,
    uiElementsColor: Color,
    pagerState: PagerState,
) {
    if (locationsUIStates != null) {

        LoadedData(
            locationsUIStates = locationsUIStates,
            uiElementsColor = uiElementsColor,
            pagerState = pagerState,
        )
    } else {
        LoadingProcessIndicator()
    }
}

@Composable
private fun LoadedData(
    locationsUIStates: List<LocationUIState>,
    uiElementsColor: Color,
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
                shouldResetScroll = page != pagerState.currentPage,
                uiElementsColor = uiElementsColor,
            )
        }

        PageIndicator(
            modifier = pagerIndicatorModifier,
            pagerState = pagerState,
            uiElementsColor = uiElementsColor,
        )
    }
}

@Composable
private fun LocationPage(
    currentLocation: LocationUIState,
    shouldResetScroll: Boolean,
    uiElementsColor: Color,
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
                shouldResetScroll = shouldResetScroll,
                backgroundColor = uiElementsColor
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )

            DailyForecastSection(
                dailyForecasts = currentLocation.dailyForecasts,
                shouldResetScroll = shouldResetScroll,
                backgroundColor = uiElementsColor
            )
        }
    }
}

@Composable
private fun PageIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    uiElementsColor: Color,
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
                uiElementsColor
            }

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(7.5.dp)
            )
        }
    }
}