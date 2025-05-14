package com.example.weathercompose.ui.compose.forecast_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.theme.SiberianIce

@Composable
fun DailyForecastSection(
    dailyForecasts: List<DailyForecastItem>,
    backgroundColor: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(size = 17.dp))
            .background(color = backgroundColor),
    ) {
        IconWithLabelHorizontal(
            iconRes = R.drawable.calendar_svgrepo_com,
            labelText = "Daily forecast",
            iconTint = Color.White
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 10.dp),
            thickness = 1.3.dp,
            color = Color.White
        )
        DailyForecastList(dailyForecasts = dailyForecasts)
    }
}

@Composable
fun DailyForecastList(
    dailyForecasts: List<DailyForecastItem>,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(7.5.dp),
    ) {
        items(dailyForecasts) { item ->
            DailyForecastListItem(
                dailyForecastItem = item,
            )
        }
    }
}

@Composable
fun DailyForecastListItem(
    dailyForecastItem: DailyForecastItem,
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
            fontSize = 16.sp,
            text = dailyForecastItem.date,
            textAlign = TextAlign.Center,
            color = Color.White,
        )

        Text(
            fontSize = 14.sp,
            text = dailyForecastItem.dayOfMonth,
            textAlign = TextAlign.Center,
            color = Color.White,
        )

        Icon(
            painter = painterResource(id = dailyForecastItem.weatherIconRes),
            contentDescription = "The weather icon",
            modifier = Modifier.size(35.dp),
            tint = Color.White,
        )

        Text(
            text = "${dailyForecastItem.maxTemperature}°",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "${dailyForecastItem.minTemperature}°",
            modifier = Modifier.padding(bottom = 15.dp),
            color = SiberianIce,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}