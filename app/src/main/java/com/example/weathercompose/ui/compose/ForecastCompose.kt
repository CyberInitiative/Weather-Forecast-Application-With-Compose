package com.example.weathercompose.ui.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathercompose.R
import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.model.HourlyForecastItem
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.ui_state.CityForecastUIState
import com.example.weathercompose.ui.ui_state.CityForecastUIState.CityDataUIState
import com.example.weathercompose.ui.ui_state.DailyForecastDataUIState
import com.example.weathercompose.ui.ui_state.HourlyForecastDataUIState
import com.example.weathercompose.ui.viewmodel.MainViewModel
import java.time.format.TextStyle
import java.util.Locale

private const val TAG = "ForecastCompose"

private const val COLOR_TRANSITION_ANIMATION_DURATION: Int = 700

@Composable
fun ForecastContent(
    viewModel: MainViewModel,
    onAppearanceStateChange: (PrecipitationCondition) -> Unit
    //TODO add navigate function as parameter for search city screen navigation;
) {

    val cityForecastUIState by viewModel.cityForecastUIState.collectAsState()

    val context = LocalContext.current

    val animatedRowColor by animateColorAsState(
        targetValue = Color(context.getColor(viewModel.rowColor)),
        animationSpec = tween(durationMillis = COLOR_TRANSITION_ANIMATION_DURATION)
    )

    LaunchedEffect(Unit) {
        viewModel.precipitationCondition.collect { state ->
            onAppearanceStateChange(state)

            when (state) {
                PrecipitationCondition.NO_PRECIPITATION_DAY -> {
                    viewModel.rowColor = R.color.liberty
                }

                PrecipitationCondition.NO_PRECIPITATION_NIGHT -> {
                    viewModel.rowColor = R.color.mesmerize
                }

                PrecipitationCondition.PRECIPITATION_DAY -> {
                    viewModel.rowColor = R.color.hilo_bay_25_percent_darker
                }

                PrecipitationCondition.PRECIPITATION_NIGHT -> {
                    viewModel.rowColor = R.color.english_channel_10_percent_darker
                }
            }
        }
    }

    // TODO If we have no cities, we need move to search city screen;
//    LaunchedEffect(hasSavedCities) {
//        if (!hasSavedCities) {
//            Log.d(TAG, "has no saved cities!")
//            navController.navigate(NavigationRoutes.CitySearch) {
//                popUpTo(NavigationRoutes.Forecast) {
//                    inclusive = true
//                }
//            }
//        }
//    }

//    val backGroundColor =
//        if (appearanceState == PrecipitationsAndTimeOfDayState.NO_PRECIPITATIONS_DAY) {
//            colorResource(R.color.castle_moat)
//        } else {
//            colorResource(R.color.deep_royal)
//        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (cityForecastUIState) {
            is CityDataUIState -> {
                val cityDataUIState = cityForecastUIState as CityDataUIState

                if (!cityDataUIState.isDataLoading) {
                    Text(
                        text = (cityForecastUIState as CityDataUIState).cityName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp)
                            .padding(start = 15.dp),
                        color = Color.White,
                        fontSize = 30.sp,
                    )

                    when (cityDataUIState.hourlyForecastsUIState) {
                        is HourlyForecastDataUIState.HourlyForecastDataPresentUIState -> {
                            HourlyForecastList(
                                hourlyForecasts = cityDataUIState.hourlyForecastsUIState.hourlyForecastItems,
                                backgroundColor = animatedRowColor
                            )
                        }

                        is HourlyForecastDataUIState.NoActualForecastDataUIState -> {
                            //TODO add text explaining no data presence
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                    )

                    when (cityDataUIState.dailyForecastsUIState) {
                        is DailyForecastDataUIState.DailyForecastDataPresentUIState -> {
                            DailyForecastList(
                                dailyForecasts = cityDataUIState.dailyForecastsUIState.dailyForecastItems,
                                backgroundColor = animatedRowColor
                            )
                        }

                        is DailyForecastDataUIState.NoActualForecastDataUIState -> {
                            //TODO add text explaining no data presence
                        }
                    }

                } else {
                    LoadingProcessIndicator()
                }

            }

            is CityForecastUIState.ErrorForecastUIState -> TODO()
            CityForecastUIState.NoCityDataForecastUIState -> TODO()
        }
    }
}

@Composable
fun HourlyForecastList(
    hourlyForecasts: List<HourlyForecastItem>,
    backgroundColor: Color,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(15.dp)
            ),
    ) {
        items(hourlyForecasts) { item ->
            HourlyForecastListItem(item)
        }
    }
}

@Composable
fun HourlyForecastListItem(hourlyForecastItem: HourlyForecastItem) {
    Column(
        modifier = Modifier
            .width(90.dp)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
            fontSize = 14.sp,
            text = hourlyForecastItem.time.toString(),
            textAlign = TextAlign.Center,
            color = Color.White,
        )

        Image(
            painter = painterResource(id = hourlyForecastItem.weatherIconRes),
            contentDescription = "The ${hourlyForecastItem.weatherDescription} weather icon",
            modifier = Modifier.size(45.dp),
        )

        WeatherDescriptionLabel(text = stringResource(hourlyForecastItem.weatherDescription))

        Text(
            modifier = Modifier.padding(top = 2.5.dp, bottom = 20.dp),
            fontSize = 16.sp,
            text = stringResource(R.string.temperature_label, hourlyForecastItem.temperature),
            textAlign = TextAlign.Center,
            color = Color.White,
        )
    }
}

@Composable
fun DailyForecastList(
    dailyForecasts: List<DailyForecastItem>,
    backgroundColor: Color,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(15.dp)
            ),
    ) {
        items(dailyForecasts) { item ->
            DailyForecastListItem(item)
        }
    }
}

@Composable
fun DailyForecastListItem(dailyForecastItem: DailyForecastItem) {
    Column(
        modifier = Modifier
            .width(90.dp)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val date = dailyForecastItem.date

        Text(
            modifier = Modifier.padding(top = 20.dp, bottom = 2.5.dp),
            fontSize = 14.sp,
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US),
            textAlign = TextAlign.Center,
            color = Color.White,
        )

        Text(
            modifier = Modifier.padding(top = 2.5.dp, bottom = 10.dp),
            fontSize = 12.sp,
            text = "${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.SHORT, Locale.US)}",
            textAlign = TextAlign.Center,
            color = Color.White,
        )

        Image(
            painter = painterResource(id = dailyForecastItem.weatherIconRes),
            contentDescription = "The ${dailyForecastItem.weatherDescription} weather icon",
            modifier = Modifier.size(45.dp),
        )

        WeatherDescriptionLabel(text = stringResource(dailyForecastItem.weatherDescription))

        Text(
            modifier = Modifier.padding(top = 10.dp, bottom = 2.5.dp),
            fontSize = 16.sp,
            text = stringResource(R.string.max_temperature_label, dailyForecastItem.maxTemperature),
            textAlign = TextAlign.Center,
            color = Color.White,
        )

        Text(
            modifier = Modifier.padding(top = 2.5.dp, bottom = 20.dp),
            fontSize = 16.sp,
            text = stringResource(R.string.min_temperature_label, dailyForecastItem.minTemperature),
            textAlign = TextAlign.Center,
            color = Color.White,
        )
    }
}

@Composable
fun WeatherDescriptionLabel(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        )
    }
}
