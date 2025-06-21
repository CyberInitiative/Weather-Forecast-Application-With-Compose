package com.example.weathercompose.ui.compose.forecast_screen

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathercompose.R
import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.domain.model.forecast.DataState
import com.example.weathercompose.ui.compose.location_search_screen.LoadingProcessIndicator
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
import com.example.weathercompose.ui.ui_state.LocationUIState
import com.example.weathercompose.ui.viewmodel.ForecastViewModel

@Suppress("unused")
private const val TAG = "ForecastCompose"

@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel,
    locationId: Long?,
    widgetsBackgroundColor: Color,
    onWeatherAndDayTimeStateChange: (WeatherAndDayTimeState) -> Unit,
    onNavigateToLocationSearchScreen: () -> Unit,
    onLocationNameSet: (String) -> Unit,
    onLocationNameVisibilityChange: (Boolean) -> Unit,
) {
    val locationsUIStates by viewModel.locationsUIStates.collectAsState()
    val weatherAndDayTimeState by viewModel.weatherAndDayTimeState.collectAsState()
    val settingsTemperatureUnit by viewModel.settingsTemperatureUnit.collectAsState()

    val locationsData = (locationsUIStates as? DataState.Ready)?.data
    val pagerState = rememberPagerState(pageCount = { locationsData?.size ?: 0 })

    LaunchedEffect(locationsUIStates) {
        if (locationsUIStates is DataState.NoData) {
            onNavigateToLocationSearchScreen()
        }
    }
    LaunchedEffect(locationId, locationsData) {
        if (locationsData.isNullOrEmpty() || locationId == null) return@LaunchedEffect

        val index = locationsData.indexOfFirst { it.id == locationId }
        if (index != -1) {
            pagerState.scrollToPage(index)
        }
    }
    LaunchedEffect(pagerState, locationsData) {
        if (locationsData?.isNotEmpty() == true) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                viewModel.onPageSelected(page)
            }
        }
    }
    LaunchedEffect(weatherAndDayTimeState) {
        onWeatherAndDayTimeStateChange(weatherAndDayTimeState)
    }

    ForecastContent(
        locationsUIStates = locationsUIStates,
        pagerState = pagerState,
        widgetsBackgroundColor = widgetsBackgroundColor,
        settingsTemperatureUnit = settingsTemperatureUnit,
        onLocationNameSet = onLocationNameSet,
        onLocationNameVisibilityChange = onLocationNameVisibilityChange,
    )
}

@Composable
private fun ForecastContent(
    locationsUIStates: DataState<List<LocationUIState>>,
    pagerState: PagerState,
    widgetsBackgroundColor: Color,
    settingsTemperatureUnit: TemperatureUnit,
    onLocationNameSet: (String) -> Unit,
    onLocationNameVisibilityChange: (Boolean) -> Unit,
) {
    when (locationsUIStates) {
        DataState.Initial, DataState.Loading -> {
            LoadingProcessIndicator()
        }

        is DataState.Ready -> {
            LoadedData(
                locationsUIStates = locationsUIStates.data,
                pagerState = pagerState,
                widgetsBackgroundColor = widgetsBackgroundColor,
                settingsTemperatureUnit = settingsTemperatureUnit,
                onLocationNameSet = onLocationNameSet,
                onLocationNameVisibilityChange = onLocationNameVisibilityChange,
            )
        }

        DataState.NoData -> Unit
        is DataState.Error -> Unit
    }
}

@Composable
private fun LoadedData(
    locationsUIStates: List<LocationUIState>,
    pagerState: PagerState,
    widgetsBackgroundColor: Color,
    settingsTemperatureUnit: TemperatureUnit,
    onLocationNameSet: (String) -> Unit,
    onLocationNameVisibilityChange: (Boolean) -> Unit,
) {
    val sharedScrollState = rememberScrollState()

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val currentLocation = locationsUIStates[page]
            val isCurrentPage = pagerState.currentPage == page

            LocationPage(
                currentLocation = currentLocation,
                isCurrentPage = isCurrentPage,
                scrollState = sharedScrollState,
                shouldResetScroll = page != pagerState.currentPage,
                widgetsBackgroundColor = widgetsBackgroundColor,
                settingsTemperatureUnit = settingsTemperatureUnit,
                onLocationNameSet = onLocationNameSet,
                onLocationNameVisibilityChange = onLocationNameVisibilityChange,

            )
        }

        PageIndicator(
            pagerState = pagerState,
            modifier = Modifier,
            unselectedPageColor = widgetsBackgroundColor,
        )
    }
}

@Composable
private fun LocationPage(
    currentLocation: LocationUIState,
    isCurrentPage: Boolean,
    scrollState: ScrollState,
    shouldResetScroll: Boolean,
    widgetsBackgroundColor: Color,
    settingsTemperatureUnit: TemperatureUnit,
    onLocationNameSet: (String) -> Unit,
    onLocationNameVisibilityChange: (Boolean) -> Unit,
) {
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(value = null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .onPlaced { layoutCoordinates: LayoutCoordinates -> coordinates = layoutCoordinates }
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .verticalScroll(state = scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (currentLocation.isLoading) {
            LoadingProcessIndicator()
        } else {
            LaunchedEffect(isCurrentPage) {
                if (isCurrentPage) {
                    onLocationNameSet(currentLocation.locationName)
                }
            }

            LocationAndWeatherInfoSection(
                locationUIState = currentLocation,
                isCurrentPage = isCurrentPage,
                settingsTemperatureUnit = settingsTemperatureUnit,
                layoutCoordinates = coordinates,
                onLocationNameVisibilityChange = onLocationNameVisibilityChange,
            )

            Spacer(modifier = Modifier.height(15.dp))

            GeneralWeatherInfo(
                currentLocation = currentLocation,
                backgroundColor = widgetsBackgroundColor,
            )

            Spacer(modifier = Modifier.height(15.dp))

            HourlyForecastSection(
                hourlyForecasts = currentLocation.hourlyForecasts,
                shouldResetScroll = shouldResetScroll,
                backgroundColor = widgetsBackgroundColor
            )

            Spacer(modifier = Modifier.height(15.dp))

            DailyForecastSection(
                dailyForecasts = currentLocation.dailyForecasts,
                shouldResetScroll = shouldResetScroll,
                backgroundColor = widgetsBackgroundColor,
                coroutineScope = coroutineScope,
            )

            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
private fun GeneralWeatherInfo(
    currentLocation: LocationUIState,
    backgroundColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(size = 17.dp))
            .background(color = backgroundColor),
    ) {
        GeneralWeatherInfoUnit(
            icon = R.drawable.wind,
            iconContentDescription = "Wind icon",
            firstLevelText = currentLocation.currentWindSpeed,
            secondLevelText = "Wind speed",
            secondLevelFontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f)
        )

        GeneralWeatherInfoUnit(
            icon = R.drawable.humidity,
            iconContentDescription = "Humidity icon",
            firstLevelText = currentLocation.currentRelativeHumidity,
            secondLevelText = "Humidity",
            secondLevelFontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f)
        )

        GeneralWeatherInfoUnit(
            icon = R.drawable.sun_sunrise,
            iconContentDescription = "Sunrise icon",
            firstLevelText = currentLocation.sunrise,
            secondLevelText = "Sunrise",
            secondLevelFontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f)
        )

        GeneralWeatherInfoUnit(
            icon = R.drawable.sun_sunset,
            iconContentDescription = "Sunset icon",
            firstLevelText = currentLocation.sunset,
            secondLevelText = "Sunset",
            secondLevelFontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
private fun GeneralWeatherInfoUnit(
    @DrawableRes
    icon: Int,
    iconContentDescription: String = "",
    iconSize: Dp = 40.dp,
    iconTint: Color = Color.White,
    firstLevelText: String,
    firstLevelTextColor: Color = Color.White,
    firstLevelTextSize: TextUnit = 13.sp,
    firstLevelFontWeight: FontWeight? = null,
    secondLevelText: String,
    secondLevelTextColor: Color = Color.White,
    secondLevelTextSize: TextUnit = 12.sp,
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
            modifier = Modifier
                .width(65.dp),
            color = secondLevelTextColor,
            fontSize = secondLevelTextSize,
            fontWeight = secondLevelFontWeight,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
        )
    }
}

@Composable
private fun PageIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    unselectedPageColor: Color,
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
                unselectedPageColor
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