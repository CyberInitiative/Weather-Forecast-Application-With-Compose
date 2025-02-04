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
import androidx.navigation.NavController
import com.example.weathercompose.R
import com.example.weathercompose.ui.UIState
import com.example.weathercompose.ui.model.CityUIModel
import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.model.HourlyForecastItem
import com.example.weathercompose.ui.viewmodel.MainViewModel

@Composable
fun ForecastContent(
    viewModel: MainViewModel,
    navController: NavController,
) {
    val currentCity by viewModel.currentCityState.collectAsState(initial = UIState.Loading())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentCity) {
            is UIState.Content -> {
                val currentCityData = (currentCity as UIState.Content<CityUIModel>).data

                Text(
                    text = currentCityData.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp)
                        .padding(start = 15.dp),
                    color = Color.White,
                    fontSize = 30.sp,
                )

                HourlyForecastList(
                    currentCityData.forecasts[0].hourlyForecasts
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                )

                DailyForecastList(
                    currentCityData.forecasts
                )
            }

            is UIState.Empty -> {}
            is UIState.Error -> {}
            is UIState.Loading -> {
                LoadingProgressBar()
            }
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
            text = hourlyForecastItem.formattedTime,
            textAlign = TextAlign.Center,
            color = Color.White,
        )

        Image(
            painter = painterResource(R.drawable.blue_cloud_and_lightning_16466),
            contentDescription = "test image",
            modifier = Modifier.size(45.dp),
        )

        WeatherDescriptionLabel(text = hourlyForecastItem.weatherDescription)

        Text(
            modifier = Modifier.padding(top = 2.5.dp, bottom = 20.dp),
            fontSize = 16.sp,
            text = hourlyForecastItem.temperature.toString(),
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
        Text(
            modifier = Modifier.padding(top = 20.dp, bottom = 2.5.dp),
            fontSize = 14.sp,
            text = dailyForecastItem.dayNameInWeek,
            textAlign = TextAlign.Center,
            color = Color.White,
        )

        Text(
            modifier = Modifier.padding(top = 2.5.dp, bottom = 10.dp),
            fontSize = 12.sp,
            text = dailyForecastItem.monthAndDayNumber,
            textAlign = TextAlign.Center,
            color = Color.White,
        )

        Image(
            painter = painterResource(R.drawable.blue_cloud_and_lightning_16466),
            contentDescription = "test image",
            modifier = Modifier.size(45.dp),
        )

        WeatherDescriptionLabel(text = dailyForecastItem.weatherDescription)

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
