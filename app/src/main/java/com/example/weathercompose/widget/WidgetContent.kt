package com.example.weathercompose.widget

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentSize
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.weathercompose.R
import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.domain.mapper.mapToDailyForecastItem
import com.example.weathercompose.domain.mapper.mapToHourlyForecastItem
import com.example.weathercompose.domain.repository.ForecastRepository
import com.example.weathercompose.domain.repository.LocationRepository
import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.model.HourlyForecastItem
import com.example.weathercompose.utils.getCurrentDateInTimeZone
import com.example.weathercompose.utils.getCurrentHourInTimeZone
import org.koin.compose.koinInject

@Composable
fun ForecastWidgetContent() {
    val locationRepository = koinInject<LocationRepository>()
    val forecastRepository = koinInject<ForecastRepository>()
    val homeLocation by locationRepository.observeHomeLocation().collectAsState(null)

    var dailyForecast by remember { mutableStateOf<DailyForecastItem?>(null) }
    var hourlyForecasts by remember { mutableStateOf<List<HourlyForecastItem>>(emptyList()) }

    Column(
        modifier = GlanceModifier
            .padding(horizontal = 10.dp)
            .fillMaxSize()
            .background(imageProvider = ImageProvider(R.drawable.forecast_widget_background))
    ) {
        val context = LocalContext.current

        when {
            homeLocation != null -> {
                LaunchedEffect(homeLocation) {
                    hourlyForecasts = forecastRepository.findHourlyForecastsByLocationId(
                        locationId = homeLocation!!.id,
                        date = getCurrentDateInTimeZone(timeZone = homeLocation!!.timeZone),
                        startHour = getCurrentHourInTimeZone(timeZone = homeLocation!!.timeZone),
                        limit = 6,
                    ).map {
                        it.mapToHourlyForecastItem(
                            timeZone = homeLocation!!.timeZone,
                            temperatureUnit = TemperatureUnit.CELSIUS
                        )
                    }

                    dailyForecast = forecastRepository.findDailyForecastByLocationIdAndDate(
                        locationId = homeLocation!!.id,
                        date = getCurrentDateInTimeZone(timeZone = homeLocation!!.timeZone),
                    )?.mapToDailyForecastItem(
                        timeZone = homeLocation!!.timeZone,
                        temperatureUnit = TemperatureUnit.CELSIUS
                    )
                }
                val currentTemperature = if (hourlyForecasts.isNotEmpty())
                    hourlyForecasts[0].temperature
                else "--"

                OtherData(
                    currentTemperature = currentTemperature,
                    locationName = homeLocation!!.name,
                    weatherDescription = getStr(dailyForecast?.weatherDescription, context),
                    modifier = GlanceModifier
                        .defaultWeight(),
                    dailyMaxTemperature = dailyForecast?.maxTemperature ?: "--",
                    dailyMinTemperature = dailyForecast?.minTemperature ?: "--",
                    dailyWeatherIcon = dailyForecast?.weatherIconRes
                )
                HourlyForecastRow(
                    hourlyForecasts = hourlyForecasts,
                    modifier = GlanceModifier
                        .defaultWeight()
                )
            }

            else -> hourlyForecasts = emptyList()
        }
    }
}

fun getStr(@StringRes description: Int?, context: Context): String {
    return if (description != null) {
        context.getString(description)
    } else {
        "--"
    }
}

@Composable
fun OtherData(
    currentTemperature: String,
    locationName: String,
    weatherDescription: String,
    modifier: GlanceModifier = GlanceModifier,
    dailyMaxTemperature: String,
    dailyMinTemperature: String,
    @DrawableRes
    dailyWeatherIcon: Int?,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetText(
            text = currentTemperature,
            modifier = GlanceModifier
                .padding(start = 8.dp),
            fontSize = 56.sp,
        )
        Spacer(modifier = GlanceModifier.width(7.5.dp))
        Column(modifier = GlanceModifier.padding(end = 10.dp).wrapContentHeight()) {
            Row(
                modifier = GlanceModifier
                    .height(35.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WidgetText(
                    text = locationName,
                    modifier = GlanceModifier
                        .defaultWeight(),
                    fontSize = 14.sp
                )

                Spacer(modifier = GlanceModifier.defaultWeight())

                if (dailyWeatherIcon != null) {
                    Image(
                        provider = ImageProvider(dailyWeatherIcon),
                        contentDescription = "Weather icon",
                        modifier = GlanceModifier.size(30.dp),
                        colorFilter = ColorFilter.tint(colorProvider = ColorProvider(color = Color.White))
                    )
                }
            }
            Row(
                modifier = GlanceModifier
                    .height(25.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WidgetText(text = weatherDescription, fontSize = 14.sp)
                Spacer(modifier = GlanceModifier.defaultWeight())
                WidgetText(
                    text = "$dailyMaxTemperature / $dailyMinTemperature",
                    modifier = GlanceModifier.wrapContentWidth(),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun HourlyForecastRow(
    hourlyForecasts: List<HourlyForecastItem>,
    modifier: GlanceModifier = GlanceModifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        //the first hourly forecast item is the current time forecast,
        //we want show it on the top of the widget, so we skip it here.
        for (i in 1 until hourlyForecasts.size) {
            HourlyForecastItem(
                hourlyForecastItem = hourlyForecasts[i],
                modifier = GlanceModifier.defaultWeight()
            )
            if (i != hourlyForecasts.lastIndex) {
                Spacer(modifier = GlanceModifier.width(8.dp))
            }
        }
    }
}

@Composable
fun HourlyForecastItem(
    hourlyForecastItem: HourlyForecastItem,
    modifier: GlanceModifier = GlanceModifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WidgetText(text = hourlyForecastItem.time)
        Spacer(modifier = GlanceModifier.height(5.dp))
        Image(
            provider = ImageProvider(hourlyForecastItem.weatherIconRes),
            contentDescription = "Weather icon",
            modifier = GlanceModifier.size(30.dp),
            colorFilter = ColorFilter.tint(colorProvider = ColorProvider(color = Color.White))
        )
        Spacer(modifier = GlanceModifier.height(5.dp))
        WidgetText(text = hourlyForecastItem.temperature)
    }
}

@Composable
fun WidgetText(
    text: String,
    modifier: GlanceModifier = GlanceModifier,
    fontSize: TextUnit = 12.sp,
    style: TextStyle = TextStyle(
        color = ColorProvider(Color.White),
        fontSize = fontSize
    )
) {
    Text(
        text = text,
        modifier = modifier
            .wrapContentSize(),
        style = style
    )
}