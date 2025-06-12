package com.example.weathercompose.widget

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
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
import com.example.weathercompose.data.model.widget.WidgetHourlyForecast
import com.example.weathercompose.data.model.widget.WidgetLocationWithForecasts
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import com.example.weathercompose.domain.repository.WidgetLocationRepository
import com.example.weathercompose.domain.usecase.settings.GetCurrentTemperatureUnitUseCase
import com.example.weathercompose.ui.activity.MainActivity
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
import com.example.weathercompose.widget.PrefKeys.LOCATION_ID_KEY
import com.example.weathercompose.widget.PrefKeys.TEMPERATURE_UNIT_KEY
import org.koin.compose.koinInject

const val LOCATION_ID_PARAM = "location_id"

@Composable
fun ForecastWidgetContent(preferences: Preferences) {
    val context = LocalContext.current
    val widgetForecastRepository = koinInject<WidgetLocationRepository>()
    val getCurrentTemperatureUnitUseCase: GetCurrentTemperatureUnitUseCase = koinInject()
    var locationWithForecasts by remember {
        mutableStateOf<WidgetLocationWithForecasts?>(value = null)
    }

    @DrawableRes
    var backgroundRes by remember {
        mutableIntStateOf(value = R.drawable.no_precipitation_day_widget_background)
    }

    val temperatureUnitInSettings by getCurrentTemperatureUnitUseCase().collectAsState(
        initial = TemperatureUnit.CELSIUS
    )

    val widgetLocationId = preferences[LOCATION_ID_KEY] ?: 0L
    val widgetTemperatureUnit = preferences[TEMPERATURE_UNIT_KEY]
        ?.let { unitName -> WidgetTemperatureUnit.valueOf(unitName) }
        ?: WidgetTemperatureUnit.CELSIUS

    LaunchedEffect(widgetLocationId) {
        locationWithForecasts = widgetForecastRepository.findWidgetLocationsWithForecastsById(
            locationId = widgetLocationId
        )
        locationWithForecasts?.getPrecipitationsAndTimeOfDayStateForCurrentHour()?.let { state ->
            backgroundRes = when (state) {
                WeatherAndDayTimeState.NO_PRECIPITATION_DAY -> {
                    R.drawable.no_precipitation_day_widget_background
                }

                WeatherAndDayTimeState.NO_PRECIPITATION_NIGHT -> {
                    R.drawable.no_precipitation_night_widget_background
                }

                WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_DAY -> {
                    R.drawable.overcast_or_precipitation_day_widget_background
                }

                WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_NIGHT -> {
                    R.drawable.overcast_or_precipitation_night_widget_background
                }
            }
        }
    }

    Column(
        modifier = GlanceModifier
            .padding(horizontal = 10.dp)
            .fillMaxSize()
            .clickable(
                actionStartActivity<MainActivity>(
                    parameters = actionParametersOf(
                        ActionParameters.Key<Long>(LOCATION_ID_PARAM) to widgetLocationId
                    )
                )
            )
            .background(
                imageProvider = ImageProvider(
                    resId = backgroundRes
                )
            )
    ) {
        when {
            locationWithForecasts != null -> {
                val currentHour =
                    locationWithForecasts?.hourlyForecasts?.firstOrNull()

                OtherData(
                    currentTemperature = getTemperature(
                        temperature = currentHour?.temperature,
                        widgetTemperatureUnit = widgetTemperatureUnit,
                        settingsTemperatureUnit = temperatureUnitInSettings
                    ),
                    locationName = locationWithForecasts?.locationName ?: "",
                    weatherDescription = getWeatherDescription(
                        weatherDescription = currentHour?.weatherDescription,
                        context = context
                    ),
                    modifier = GlanceModifier
                        .defaultWeight(),
                    dailyMaxTemperature = getTemperature(
                        temperature = locationWithForecasts?.dailyMaxTemperature,
                        widgetTemperatureUnit = widgetTemperatureUnit,
                        settingsTemperatureUnit = temperatureUnitInSettings
                    ),
                    dailyMinTemperature = getTemperature(
                        temperature = locationWithForecasts?.dailyMinTemperature,
                        widgetTemperatureUnit = widgetTemperatureUnit,
                        settingsTemperatureUnit = temperatureUnitInSettings
                    ),
                    dailyWeatherIcon = WeatherDescription.weatherDescriptionToIconRes(
                        currentHour?.weatherDescription,
                    )
                )
                HourlyForecastRow(
                    hourlyForecasts = locationWithForecasts?.hourlyForecasts ?: emptyList(),
                    modifier = GlanceModifier
                        .defaultWeight(),
                    widgetTemperatureUnit = widgetTemperatureUnit,
                    temperatureUnitInSettings = temperatureUnitInSettings
                )
            }
        }
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
    hourlyForecasts: List<WidgetHourlyForecast>,
    modifier: GlanceModifier = GlanceModifier,
    widgetTemperatureUnit: WidgetTemperatureUnit,
    temperatureUnitInSettings: TemperatureUnit,
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
                modifier = GlanceModifier.defaultWeight(),
                widgetTemperatureUnit = widgetTemperatureUnit,
                settingsTemperatureUnit = temperatureUnitInSettings,
            )
            if (i != hourlyForecasts.lastIndex) {
                Spacer(modifier = GlanceModifier.width(8.dp))
            }
        }
    }
}

@Composable
fun HourlyForecastItem(
    hourlyForecastItem: WidgetHourlyForecast,
    modifier: GlanceModifier = GlanceModifier,
    widgetTemperatureUnit: WidgetTemperatureUnit,
    settingsTemperatureUnit: TemperatureUnit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WidgetText(text = hourlyForecastItem.time)
        Spacer(modifier = GlanceModifier.height(5.dp))
        Image(
            provider = ImageProvider(
                WeatherDescription.weatherDescriptionToIconRes(
                    hourlyForecastItem.weatherDescription
                )
            ),
            contentDescription = "Weather icon",
            modifier = GlanceModifier.size(30.dp),
            colorFilter = ColorFilter.tint(colorProvider = ColorProvider(color = Color.White))
        )
        Spacer(modifier = GlanceModifier.height(5.dp))
        WidgetText(
            text = getTemperature(
                temperature = hourlyForecastItem.temperature,
                widgetTemperatureUnit = widgetTemperatureUnit,
                settingsTemperatureUnit = settingsTemperatureUnit,
            )
        )
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

fun getTemperature(
    temperature: Double?,
    widgetTemperatureUnit: WidgetTemperatureUnit,
    settingsTemperatureUnit: TemperatureUnit,
): String {
    return when {
        temperature == null -> "--"
        else -> {
            return when (widgetTemperatureUnit) {
                WidgetTemperatureUnit.CELSIUS -> {
                    TemperatureUnit.getTemperatureForUI(
                        temperature = temperature,
                        temperatureUnit = TemperatureUnit.CELSIUS
                    )
                }

                WidgetTemperatureUnit.FAHRENHEIT -> {
                    TemperatureUnit.getTemperatureForUI(
                        temperature = temperature,
                        temperatureUnit = TemperatureUnit.FAHRENHEIT
                    )
                }

                WidgetTemperatureUnit.COMPLY_WITH_SETTINGS -> {
                    TemperatureUnit.getTemperatureForUI(
                        temperature = temperature,
                        temperatureUnit = settingsTemperatureUnit
                    )
                }
            }
        }
    }
}

fun getWeatherDescription(weatherDescription: WeatherDescription?, context: Context): String {
    return if (weatherDescription != null) {
        context.getString(WeatherDescription.weatherDescriptionToString(weatherDescription))
    } else {
        "--"
    }
}