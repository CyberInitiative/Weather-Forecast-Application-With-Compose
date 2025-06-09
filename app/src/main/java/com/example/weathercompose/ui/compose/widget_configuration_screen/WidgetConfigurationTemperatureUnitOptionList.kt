package com.example.weathercompose.ui.compose.widget_configuration_screen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathercompose.R
import com.example.weathercompose.ui.theme.Liberty
import com.example.weathercompose.ui.theme.SiberianIce
import com.example.weathercompose.widget.WidgetTemperatureUnit

@Composable
fun WidgetConfigurationTemperatureUnitOptionsList(
    selectedTemperatureUnit: WidgetTemperatureUnit,
    onTemperatureUnitOptionClick: (WidgetTemperatureUnit) -> Unit,
) {
    val options = arrayOf(
        WidgetTemperatureUnit.CELSIUS to R.string.celsius_unit,
        WidgetTemperatureUnit.FAHRENHEIT to R.string.fahrenheit_unit,
        WidgetTemperatureUnit.COMPLY_WITH_SETTINGS to R.string.comply_with_settings_unit
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 15.dp)
            .clip(shape = RoundedCornerShape(17.dp))
            .background(color = Liberty)
    ) {
        options.forEachIndexed { index, (temperatureUnit, optionLabel) ->
            WidgetConfigurationTemperatureUnitOption(
                optionLabel = optionLabel,
                widgetTemperatureUnit = temperatureUnit,
                onTemperatureUnitOptionClick = onTemperatureUnitOptionClick,
                selectedTemperatureUnit = selectedTemperatureUnit
            )
            if (index != options.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    color = SiberianIce,
                    thickness = 1.2.dp
                )
            }
        }
    }
}

@Composable
private fun WidgetConfigurationTemperatureUnitOption(
    @StringRes
    optionLabel: Int,
    widgetTemperatureUnit: WidgetTemperatureUnit,
    onTemperatureUnitOptionClick: (WidgetTemperatureUnit) -> Unit,
    selectedTemperatureUnit: WidgetTemperatureUnit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                onTemperatureUnitOptionClick(widgetTemperatureUnit)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(optionLabel),
            modifier = Modifier.padding(start = 15.dp),
            color = Color.White,
            fontSize = 18.sp,
        )
        RadioButton(
            selected = widgetTemperatureUnit == selectedTemperatureUnit,
            onClick = null,
            modifier = Modifier.padding(end = 15.dp),
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.White,
                unselectedColor = SiberianIce,
            )
        )
    }
}