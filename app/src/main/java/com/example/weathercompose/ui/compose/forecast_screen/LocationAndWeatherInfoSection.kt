package com.example.weathercompose.ui.compose.forecast_screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.weathercompose.ui.ui_state.LocationUIState

@Composable
fun LocationAndWeatherInfoSection(
    locationUIState: LocationUIState,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (
            locationName,
            dayOfWeekAndDate,
            currentTemperature,
            weatherStatus,
            maxAndMinTemperature,
        ) = createRefs()

        val locationNameModifier = Modifier.constrainAs(locationName) {
            top.linkTo(parent.top)
            start.linkTo(parent.start, margin = 10.dp)
        }

        val dateOfWeekAndDateModifier = Modifier.constrainAs(dayOfWeekAndDate) {
            top.linkTo(locationName.bottom, margin = 3.5.dp)
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

        val weatherStatusModifier = Modifier.constrainAs(weatherStatus) {
            top.linkTo(currentTemperature.top, margin = 5.dp)
            start.linkTo(currentTemperature.end, margin = 10.dp)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }

        Column(modifier = locationNameModifier) {
            LocationText(name = locationUIState.locationName)
            LocationText(name = locationUIState.locationCountry)
        }

        Text(
            text = locationUIState.currentDayOfWeekAndDate,
            modifier = dateOfWeekAndDateModifier.wrapContentWidth(),
            color = Color.White,
            fontSize = 16.sp,
        )

        Text(
            text = "${locationUIState.currentDayMinTemperature} / ${locationUIState.currentDayMaxTemperature}",
            modifier = maxAndMinTemperatureModifier,
            color = Color.White,
            fontSize = 25.sp,
        )

        CurrentHourTemperature(
            temperature = locationUIState.currentHourTemperature,
            modifier = currentTemperatureModifier,
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

@Composable
private fun LocationText(
    name: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .height(35.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppText(
            text = name,
            modifier = modifier
                .fillMaxWidth(),
            fontSize = 28.sp,
        )
    }
}

@Composable
private fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight? = null,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth(),
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
    )
}

@Composable
private fun CurrentHourTemperature(
    temperature: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = temperature,
        modifier = modifier,
        color = Color.White,
        fontSize = 70.sp,
    )
}

@Composable
fun ImageWithLabelHorizontal(
    @DrawableRes imageRes: Int,
    labelText: String,
    textSize: TextUnit = 16.sp,
    modifier: Modifier = Modifier,
    marginFromIconToText: Dp = 10.dp,
    imageSize: Dp = 35.dp,
) {
    ConstraintLayout(modifier = modifier) {
        val (icon, text) = createRefs()

        val iconModifier = Modifier.constrainAs(icon) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
        }

        val textModifier = Modifier.constrainAs(text) {
            top.linkTo(icon.top)
            bottom.linkTo(icon.bottom)
            start.linkTo(icon.end, margin = marginFromIconToText)
        }

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Clock icon",
            modifier = iconModifier.size(imageSize),
        )

        Text(
            text = labelText,
            modifier = textModifier,
            color = Color.White,
            fontSize = textSize,
        )
    }
}