package com.example.weathercompose.ui.compose.widget_configuration_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathercompose.ui.model.LocationOptionItem
import com.example.weathercompose.ui.theme.Liberty
import com.example.weathercompose.ui.theme.SiberianIce

@Composable
fun WidgetConfigurationLocationOptionList(
    selectedLocationId: Long?,
    locations: List<LocationOptionItem>,
    onLocationOptionItemClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 15.dp)
            .clip(shape = RoundedCornerShape(17.dp))
            .background(color = Liberty)
    ) {
        items(
            items = locations,
            key = { location ->
                location.id
            }
        ) { location ->
            WidgetConfigurationLocationOption(
                selectedLocationId = selectedLocationId,
                locationOption = location,
                onLocationOptionItemClick = onLocationOptionItemClick,
            )
            if (location != locations.last()) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    thickness = 1.3.dp,
                    color = SiberianIce,
                )
            }
        }
    }
}

@Composable
private fun WidgetConfigurationLocationOption(
    selectedLocationId: Long?,
    locationOption: LocationOptionItem,
    onLocationOptionItemClick: (Long) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                onLocationOptionItemClick(locationOption.id)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = locationOption.locationName,
            modifier = Modifier.padding(start = 15.dp),
            color = Color.White,
            fontSize = 18.sp
        )

        RadioButton(
            selected = (locationOption.id == selectedLocationId),
            modifier = Modifier
                .padding(end = 15.dp),
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.White,
                unselectedColor = SiberianIce,
            ),
            onClick = null
        )
    }
}