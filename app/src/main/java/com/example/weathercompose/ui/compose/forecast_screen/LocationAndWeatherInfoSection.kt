package com.example.weathercompose.ui.compose.forecast_screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.ui.ui_state.LocationUIState

@Composable
fun LocationAndWeatherInfoSection(
    locationUIState: LocationUIState,
    isCurrentPage: Boolean,
    settingsTemperatureUnit: TemperatureUnit,
    layoutCoordinates: LayoutCoordinates?,
    onLocationNameVisibilityChange: (Boolean) -> Unit,
) {
    val maxTemp = locationUIState.currentDayMaxTemperature
    val minTemp = locationUIState.currentDayMinTemperature
    val minAndMaxTemp = "$minTemp / $maxTemp"

    val temperatureUnitSign = when(settingsTemperatureUnit) {
        TemperatureUnit.CELSIUS -> "°C"
        TemperatureUnit.FAHRENHEIT -> "°F"
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (
            locationName,
            locationCountry,
            dayOfWeekAndDate,
            currentTemperature,
            temperatureUnit,
            weatherStatus,
            maxAndMinTemperature,
        ) = createRefs()

        val locationNameModifier = Modifier.constrainAs(locationName) {
            top.linkTo(parent.top)
            start.linkTo(parent.start, margin = 10.dp)
        }

        val locationCountryModifier = Modifier.constrainAs(locationCountry) {
            top.linkTo(locationName.bottom)
            start.linkTo(parent.start, margin = 10.dp)
        }

        val dateOfWeekAndDateModifier = Modifier.constrainAs(dayOfWeekAndDate) {
            top.linkTo(locationCountry.bottom, margin = 3.5.dp)
            start.linkTo(parent.start, margin = 10.dp)
        }

        val maxAndMinTemperatureModifier = Modifier.constrainAs(maxAndMinTemperature) {
            top.linkTo(dayOfWeekAndDate.top)
            bottom.linkTo(dayOfWeekAndDate.bottom)
            end.linkTo(parent.end, margin = 10.dp)
        }

        val currentTemperatureModifier = Modifier.constrainAs(currentTemperature) {
            top.linkTo(dayOfWeekAndDate.bottom, margin = 20.dp)
            start.linkTo(parent.start, margin = 10.dp)
        }

        val temperatureUnitModifier = Modifier.constrainAs(temperatureUnit) {
            top.linkTo(currentTemperature.top)
            start.linkTo(currentTemperature.end)
        }

        val weatherStatusModifier = Modifier.constrainAs(weatherStatus) {
            top.linkTo(currentTemperature.top, margin = 5.dp)
            start.linkTo(temperatureUnit.end, margin = 10.dp)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }

        Text(
            text = if (locationUIState.locationCountry.isEmpty())
                locationUIState.locationName
            else {
                "${locationUIState.locationName},"
            },
            modifier = locationNameModifier
                .fillMaxWidth()
                .height(35.dp)
                .then(
                    if (isCurrentPage) {
                        Modifier.isVisible(
                            parentCoordinates = layoutCoordinates,
                            onVisibilityChange = { isVisible ->
                                onLocationNameVisibilityChange(isVisible)
                            }
                        )
                    } else {
                        Modifier
                    }
                ),
            color = Color.White,
            fontSize = 28.sp,
        )

        Text(
            text = locationUIState.locationCountry,
            modifier = locationCountryModifier
                .fillMaxWidth()
                .height(30.dp),
            color = Color.White,
            fontSize = 22.sp,
        )

        Text(
            text = locationUIState.currentDayOfWeekAndDate,
            modifier = dateOfWeekAndDateModifier.wrapContentWidth(),
            color = Color.White,
            fontSize = 16.sp,
        )

        Text(
            text = minAndMaxTemp,
            modifier = maxAndMinTemperatureModifier,
            color = Color.White,
            fontSize = 25.sp,
        )

        Text(
            text = locationUIState.currentHourTemperature,
            modifier = currentTemperatureModifier,
            color = Color.White,
            fontSize = 70.sp,
        )

        Text(
            text = temperatureUnitSign,
            modifier = temperatureUnitModifier,
            color = Color.White,
            fontSize = 45.sp,
        )

        Text(
            text = locationUIState.currentHourWeatherStatus,
            modifier = weatherStatusModifier.fillMaxWidth(),
            color = Color.White,
            fontSize = 20.sp,
            textAlign = TextAlign.Left,
            maxLines = 3,
        )
    }
}

private fun Modifier.isVisible(
    parentCoordinates: LayoutCoordinates?,
    onVisibilityChange: (Boolean) -> Unit
) = composed {

    Modifier.onGloballyPositioned { childCoordinates ->
        if (parentCoordinates == null ||
            !childCoordinates.isAttached ||
            !parentCoordinates.isAttached
        ) return@onGloballyPositioned

        val childBounds = childCoordinates.boundsInWindow()
        val parentBounds = parentCoordinates.boundsInWindow()

        val isPartiallyVisible = childBounds.overlaps(parentBounds)

        onVisibilityChange(isPartiallyVisible)
    }
}