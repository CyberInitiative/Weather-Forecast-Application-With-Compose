package com.example.weathercompose.ui.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.weathercompose.ui.model.LocationItem
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
import com.example.weathercompose.ui.theme.HiloBay25PerDarker
import com.example.weathercompose.ui.theme.Liberty
import com.example.weathercompose.ui.theme.MediumDarkShadeCyanBlue
import com.example.weathercompose.ui.theme.Solitaire5PerDarker
import com.example.weathercompose.ui.viewmodel.ForecastViewModel

private const val ON_DELETE_SWIPE = "ON_DELETE_SWIPE"

@Composable
fun LocationManagerContent(
    viewModel: ForecastViewModel,
    weatherAndDayTimeState: WeatherAndDayTimeState,
    onNavigateToSearchScreen: () -> Unit,
    onNavigateToForecastScreen: (Long) -> Unit,
) {
    val locationItems by viewModel.locationItems.collectAsState()

    var listItemAndAddButtonColor by remember { mutableStateOf(Liberty) }
    when (weatherAndDayTimeState) {
        WeatherAndDayTimeState.NO_PRECIPITATION_DAY -> {
            listItemAndAddButtonColor = Liberty
        }

        WeatherAndDayTimeState.NO_PRECIPITATION_NIGHT -> {
            listItemAndAddButtonColor = MediumDarkShadeCyanBlue
        }

        WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_DAY -> {
            listItemAndAddButtonColor = HiloBay25PerDarker
        }

        WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_NIGHT -> {
            listItemAndAddButtonColor = Solitaire5PerDarker
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        val (locationList, addLocationButton) = createRefs()
        val locationListModifier = Modifier.constrainAs(locationList) {
            top.linkTo(parent.top)
            bottom.linkTo(addLocationButton.top, margin = 20.dp)
            height = Dimension.fillToConstraints
        }
        val addLocationButtonModifier = Modifier.constrainAs(addLocationButton) {
            bottom.linkTo(parent.bottom, margin = 5.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        LocationList(
            locationItems = locationItems,
            modifier = locationListModifier,
            onLocationItemClick = onNavigateToForecastScreen,
            onLocationItemDelete = viewModel::deleteLocation,
            itemBackgroundColor = listItemAndAddButtonColor,
        )

        AddLocationButton(
            onButtonClick = onNavigateToSearchScreen,
            modifier = addLocationButtonModifier,
            backgroundColor = listItemAndAddButtonColor
        )
    }
}

@Composable
private fun LocationList(
    locationItems: List<LocationItem>,
    modifier: Modifier = Modifier,
    onLocationItemClick: (Long) -> Unit,
    onLocationItemDelete: (Long) -> Unit,
    itemBackgroundColor: Color,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(
            items = locationItems,
            key = { _, item -> item.id }
        ) { index, item ->
            SwipeToDeleteLocationItem(
                locationItem = item,
                onLocationItemClick = onLocationItemClick,
                onLocationItemDelete = onLocationItemDelete,
                itemBackgroundColor = itemBackgroundColor,
            )

            if (index != locationItems.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 8.dp,
                    color = Color.Transparent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteLocationItem(
    modifier: Modifier = Modifier,
    locationItem: LocationItem,
    onLocationItemClick: (Long) -> Unit,
    onLocationItemDelete: (Long) -> Unit,
    itemBackgroundColor: Color,
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * .25f }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        backgroundContent = {
            DeleteLocationItemBackground(swipeToDismissBoxState = swipeToDismissBoxState)
        },
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        content = {
            LocationListItem(
                locationItem = locationItem,
                onLocationItemClick = onLocationItemClick,
                itemBackgroundColor = itemBackgroundColor,
            )
        }
    )

    when (swipeToDismissBoxState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> {
            LaunchedEffect(swipeToDismissBoxState.currentValue) {
                onLocationItemDelete(locationItem.id)

                swipeToDismissBoxState.snapTo(SwipeToDismissBoxValue.Settled)
            }
        }

        SwipeToDismissBoxValue.StartToEnd,
        SwipeToDismissBoxValue.Settled -> {
            // Ignore
        }
    }
}

@Composable
private fun LocationListItem(
    locationItem: LocationItem,
    onLocationItemClick: (Long) -> Unit,
    itemBackgroundColor: Color,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clip(shape = RoundedCornerShape(15.dp))
            .background(
                color = itemBackgroundColor,
            )
            .clickable {
                onLocationItemClick(locationItem.id)
            }
    ) {
        val (weatherIcon, weatherDescriptionLabel, temperatureLabel, locationNameLabel) = createRefs()

        val weatherIconModifier = Modifier.constrainAs(weatherIcon) {
            top.linkTo(parent.top, margin = 10.dp)
            bottom.linkTo(parent.bottom, margin = 12.dp)
            end.linkTo(parent.end, margin = 10.dp)
        }

        val temperatureModifier = Modifier.constrainAs(temperatureLabel) {
            end.linkTo(weatherIcon.start, margin = 20.dp)
            top.linkTo(parent.top, margin = 10.dp)
        }

        val weatherDescriptionModifier = Modifier.constrainAs(weatherDescriptionLabel) {
            end.linkTo(weatherIcon.start, margin = 20.dp)
            top.linkTo(temperatureLabel.bottom, margin = 5.dp)
            bottom.linkTo(parent.bottom, margin = 10.dp)
        }

        val locationNameLabelModifier = Modifier.constrainAs(locationNameLabel) {
            start.linkTo(parent.start, margin = 12.dp)
            end.linkTo(weatherDescriptionLabel.start, margin = 30.dp)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
        }

        Icon(
            painter = painterResource(id = locationItem.currentHourWeatherIconRes),
            contentDescription = "Current hour weather icon",
            modifier = weatherIconModifier.size(45.dp),
            tint = Color.White,
        )

        Text(
            text = locationItem.currentHourTemperature,
            modifier = temperatureModifier,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
        )

        Text(
            text = stringResource(locationItem.currentHourWeatherDescription),
            modifier = weatherDescriptionModifier
                .width(100.dp),
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        Text(
            text = locationItem.name,
            modifier = locationNameLabelModifier.fillMaxWidth(),
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteLocationItemBackground(swipeToDismissBoxState: SwipeToDismissBoxState) {
    val color by animateColorAsState(
        targetValue = when (swipeToDismissBoxState.targetValue) {
            SwipeToDismissBoxValue.StartToEnd -> Color.Transparent

            SwipeToDismissBoxValue.Settled,
            SwipeToDismissBoxValue.EndToStart -> Color.Red
        }, label = ON_DELETE_SWIPE
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clip(shape = RoundedCornerShape(15.dp))
            .background(color = color),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = "delete",
            modifier = Modifier.padding(end = 20.dp),
        )
    }
}

@Composable
private fun AddLocationButton(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
) {
    Button(
        onClick = onButtonClick,
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight(),

        colors = ButtonColors(
            containerColor = backgroundColor,
            contentColor = Color.White,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.Gray,
        )
    ) {
        Text(
            text = "Add location",
            modifier = Modifier
                .padding(
                    vertical = 7.dp,
                    horizontal = 12.dp
                ),
            color = Color.White,
            fontSize = 18.sp,
        )
    }
}