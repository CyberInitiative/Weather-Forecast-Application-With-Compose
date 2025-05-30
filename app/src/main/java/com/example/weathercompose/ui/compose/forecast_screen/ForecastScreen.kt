package com.example.weathercompose.ui.compose.forecast_screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathercompose.R
import com.example.weathercompose.domain.model.forecast.DataState
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
        if (locationsUIStates is DataState.NoData) {
            onNavigateToLocationSearchScreen()
        }
    }

    val pagerState = rememberPagerState(pageCount = {
        (locationsUIStates as? DataState.Ready)?.data?.size ?: 0
    })

    LaunchedEffect(locationId) {
        val currentPage = pagerState.currentPage
        val locationsData = (locationsUIStates as? DataState.Ready)?.data
        val index = locationsData?.indexOfFirst { it.id == locationId } ?: currentPage
        pagerState.scrollToPage(if (index != -1) index else currentPage)
    }

    LaunchedEffect(pagerState, locationsUIStates) {
        val locationsData = (locationsUIStates as? DataState.Ready)?.data
        if (locationsData?.isNotEmpty() == true) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                viewModel.onPageSelected(page)
            }
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

    ForecastContent(
        locationsUIStates = locationsUIStates,
        uiElementsColor = uiElementsColor,
        pagerState = pagerState,
    )
}

@Composable
private fun ForecastContent(
    locationsUIStates: DataState<List<LocationUIState>>,
    uiElementsColor: Color,
    pagerState: PagerState,
) {
    when (locationsUIStates) {
        DataState.Initial, DataState.Loading -> {
            LoadingProcessIndicator()
        }

        is DataState.Ready -> {
            LoadedData(
                locationsUIStates = locationsUIStates.data,
                uiElementsColor = uiElementsColor,
                pagerState = pagerState,
            )
        }

        DataState.NoData -> {}
        is DataState.Error -> {}
    }
}

@Composable
private fun LoadedData(
    locationsUIStates: List<LocationUIState>,
    uiElementsColor: Color,
    pagerState: PagerState,
) {
    val sharedScrollState = rememberScrollState()

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f) // Fill available space
        ) { page ->
            val currentLocation = locationsUIStates[page]

            LocationPage(
                currentLocation = currentLocation,
                shouldResetScroll = page != pagerState.currentPage,
                uiElementsColor = uiElementsColor,
                scrollState = sharedScrollState,
            )
        }

        PageIndicator(
            modifier = Modifier,
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
    scrollState: ScrollState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .verticalScroll(state = scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (currentLocation.isLoading) {
            LoadingProcessIndicator()
        } else {
            LocationAndWeatherInfoSection(
                locationUIState = currentLocation
            )

            Spacer(modifier = Modifier.height(15.dp))

            AdditionalData(
                currentLocation = currentLocation,
                backgroundColor = uiElementsColor,
            )

            Spacer(modifier = Modifier.height(15.dp))

            HourlyForecastSection(
                hourlyForecasts = currentLocation.hourlyForecasts,
                shouldResetScroll = shouldResetScroll,
                backgroundColor = uiElementsColor
            )

            Spacer(modifier = Modifier.height(15.dp))

            DailyForecastSection(
                dailyForecasts = currentLocation.dailyForecasts,
                shouldResetScroll = shouldResetScroll,
                backgroundColor = uiElementsColor
            )

            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
private fun AdditionalData(
    currentLocation: LocationUIState,
    backgroundColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(size = 17.dp))
            .background(color = backgroundColor),
    ) {
        AdditionalDataUnit(
            icon = R.drawable.wind,
            iconContentDescription = "Wind icon",
            iconSize = 40.dp,
            firstLevelText = currentLocation.currentWindSpeed,
            firstLevelTextSize = 13.sp,
            secondLevelText = "Wind",
            secondLevelTextSize = 12.sp,
            secondLevelFontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f)
        )

        AdditionalDataUnit(
            icon = R.drawable.humidity,
            iconContentDescription = "Humidity icon",
            iconSize = 40.dp,
            firstLevelText = currentLocation.currentRelativeHumidity,
            firstLevelTextSize = 13.sp,
            secondLevelText = "Humidity",
            secondLevelTextSize = 12.sp,
            secondLevelFontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f)
        )

        AdditionalDataUnit(
            icon = R.drawable.sun_sunrise,
            iconContentDescription = "Sunrise icon",
            iconSize = 40.dp,
            firstLevelText = currentLocation.sunrise,
            firstLevelTextSize = 13.sp,
            secondLevelText = "Sunrise",
            secondLevelTextSize = 12.sp,
            secondLevelFontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f)
        )

        AdditionalDataUnit(
            icon = R.drawable.sun_sunset,
            iconContentDescription = "Sunset icon",
            iconSize = 40.dp,
            firstLevelText = currentLocation.sunset,
            firstLevelTextSize = 13.sp,
            secondLevelText = "Sunset",
            secondLevelTextSize = 12.sp,
            secondLevelFontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
private fun AdditionalDataUnit(
    @DrawableRes
    icon: Int,
    iconContentDescription: String = "",
    iconSize: Dp = 40.dp,
    iconTint: Color = Color.White,
    firstLevelText: String,
    firstLevelTextColor: Color = Color.White,
    firstLevelTextSize: TextUnit = 14.sp,
    firstLevelFontWeight: FontWeight? = null,
    secondLevelText: String,
    secondLevelTextColor: Color = Color.White,
    secondLevelTextSize: TextUnit = 14.sp,
    secondLevelFontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = iconContentDescription,
            modifier = Modifier.size(iconSize),
            tint = iconTint,
        )
        Text(
            text = firstLevelText,
            color = firstLevelTextColor,
            fontSize = firstLevelTextSize,
            fontWeight = firstLevelFontWeight,
        )
        Text(
            text = secondLevelText,
            color = secondLevelTextColor,
            fontSize = secondLevelTextSize,
            fontWeight = secondLevelFontWeight
        )
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
            .fillMaxWidth()
            .height(30.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
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