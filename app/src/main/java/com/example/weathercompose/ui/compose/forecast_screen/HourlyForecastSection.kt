package com.example.weathercompose.ui.compose.forecast_screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.weathercompose.R
import com.example.weathercompose.ui.model.HourlyForecastItem
import com.example.weathercompose.ui.theme.SiberianIce
import kotlinx.coroutines.launch

@Composable
fun HourlyForecastSection(
    hourlyForecasts: List<HourlyForecastItem>,
    shouldResetScroll: Boolean,
    backgroundColor: Color,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(shouldResetScroll) {
        if (shouldResetScroll) {
            scope.launch {
                listState.scrollToItem(0)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(size = 17.dp))
            .background(color = backgroundColor),
    ) {
        IconWithLabelHorizontal(
            iconRes = R.drawable.clock_nine_svgrepo_com,
            labelText = "Hourly forecast",
            iconTint = Color.White,
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 10.dp),
            thickness = 1.3.dp,
            color = Color.White
        )
        HourlyForecastList(hourlyForecasts = hourlyForecasts, state = listState)
    }
}

@Composable
fun HourlyForecastList(
    hourlyForecasts: List<HourlyForecastItem>,
    state: LazyListState,
) {
    LazyRow(
        state = state,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(size = 20.dp)),
        horizontalArrangement = Arrangement.spacedBy(7.5.dp)
    ) {
        items(hourlyForecasts) { item ->
            HourlyForecastListItem(
                hourlyForecastItem = item,
            )
        }
    }
}

@Composable
fun HourlyForecastListItem(
    hourlyForecastItem: HourlyForecastItem,
) {
    ConstraintLayout(
        modifier = Modifier
            .width(60.dp)
            .height(135.dp)
    ) {
        val (time, weatherIconWithPrecipitationProbability, temperature) = createRefs()

        val timeModifier = Modifier.constrainAs(time) {
            top.linkTo(parent.top, margin = 7.5.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        val weatherIconWithPrecipitationProbabilityModifier =
            Modifier.constrainAs(weatherIconWithPrecipitationProbability) {
                top.linkTo(time.bottom)
                bottom.linkTo(temperature.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }

        val temperatureModifier = Modifier.constrainAs(temperature) {
            bottom.linkTo(parent.bottom, margin = 7.5.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        Text(
            modifier = timeModifier,
            fontSize = 14.sp,
            text = hourlyForecastItem.time,
            textAlign = TextAlign.Center,
            color = Color.White,
        )

        WeatherIconWithPrecipitationProbability(
            precipitationProbability = hourlyForecastItem.precipitationProbability,
            weatherIcon = hourlyForecastItem.weatherIconRes,
            modifier = weatherIconWithPrecipitationProbabilityModifier,
        )

        Text(
            text = hourlyForecastItem.temperature,
            modifier = temperatureModifier,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun WeatherIconWithPrecipitationProbability(
    precipitationProbability: String,
    @DrawableRes
    weatherIcon: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (precipitationProbability.isNotEmpty()) {
            Text(
                text = precipitationProbability,
                color = SiberianIce,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
        Icon(
            painter = painterResource(id = weatherIcon),
            contentDescription = "The weather icon",
            modifier = Modifier
                .size(35.dp),
            tint = Color.White,
        )
    }
}