package com.example.weathercompose.ui.compose

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathercompose.R
import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.model.HourlyForecastItem
import com.example.weathercompose.ui.viewmodel.MainViewModel
import com.example.weathercompose.ui.viewmodel.SharedViewModel
import java.time.format.TextStyle
import java.util.Locale

private const val TAG = "ForecastCompose"

@Composable
fun ForecastContent(
    viewModel: MainViewModel,
    sharedViewModel: SharedViewModel,
    //TODO add navigate function as parameter for search city screen navigation;
) {
    val loadedCities by sharedViewModel.loadedCitiesState.collectAsState()

//    val hasSavedCities by viewModel.hasSavedCities.collectAsState()
    val forecastUIState by viewModel.forecastUIState.collectAsState()

    LaunchedEffect(loadedCities) {
        //TODO if cities empty add logic to navigate to another screen;
        viewModel.setLoadedCities(loadedCities)
    }

    //TODO If we have no cities, we need move to search city screen;
//    LaunchedEffect(hasSavedCities) {
//        if (!hasSavedCities) {
//            Log.d(TAG, "has no saved cities!")
//            navController.navigate(NavigationRoutes.CitySearch) {
//                popUpTo(NavigationRoutes.Forecast) {
//                    inclusive = true
//                } // Optional: Remove current screen from stack
//            }
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!forecastUIState.isDataLoading) {

            when {
                forecastUIState.errorMessage.isNotEmpty() -> {
                    Text(text = forecastUIState.errorMessage)
                }

                else -> {
                    Text(
                        text = forecastUIState.cityName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp)
                            .padding(start = 15.dp),
                        color = Color.White,
                        fontSize = 30.sp,
                    )

                    HourlyForecastList(
//                        forecastUIState.dailyForecasts[0].hourlyForecasts
                        emptyList()
                    )

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                    )

                    DailyForecastList(
                        forecastUIState.dailyForecasts
                    )
                }
            }
        } else {
            LoadingProcessIndicator()
        }
    }
}

@Composable
fun HourlyForecastList(dailyForecasts: List<HourlyForecastItem>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = colorResource(R.color.liberty),
                shape = RoundedCornerShape(15.dp)
            ),
    ) {
        items(dailyForecasts) { item ->
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
fun DailyForecastList(dailyForecasts: List<DailyForecastItem>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = colorResource(R.color.liberty),
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
