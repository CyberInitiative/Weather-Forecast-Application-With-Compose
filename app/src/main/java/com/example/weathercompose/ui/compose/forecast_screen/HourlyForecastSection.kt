package com.example.weathercompose.ui.compose.forecast_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.weathercompose.R
import com.example.weathercompose.ui.model.HourlyForecastItem
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
    Column(
        modifier = Modifier
            .width(60.dp)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.5.dp)
    ) {
        Text(
            modifier = Modifier.padding(top = 15.dp),
            fontSize = 14.sp,
            text = hourlyForecastItem.time.toString(),
            textAlign = TextAlign.Center,
            color = Color.White,
        )

        Icon(
            painter = painterResource(id = hourlyForecastItem.weatherIconRes),
            contentDescription = "The weather icon",
            modifier = Modifier.size(35.dp),
            tint = Color.White,
        )

        Text(
            modifier = Modifier.padding(bottom = 15.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            text = "${hourlyForecastItem.temperature}°",
            textAlign = TextAlign.Center,
            color = Color.White,
        )
    }
}