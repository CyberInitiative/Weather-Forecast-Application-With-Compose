package com.example.weathercompose.ui.compose.location_manager_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.weathercompose.R
import com.example.weathercompose.ui.model.LocationItem
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
import com.example.weathercompose.ui.theme.HiloBay25PerDarker
import com.example.weathercompose.ui.theme.Liberty
import com.example.weathercompose.ui.theme.MediumDarkShadeCyanBlue
import com.example.weathercompose.ui.theme.Solitaire5PerDarker
import com.example.weathercompose.ui.viewmodel.ForecastViewModel

@Composable
fun LocationManagerContent(
    viewModel: ForecastViewModel,
    weatherAndDayTimeState: WeatherAndDayTimeState,
    onNavigateToSearchScreen: () -> Unit,
    onNavigateToForecastScreen: (Long?) -> Unit,
) {
    BackHandler {
        onNavigateToForecastScreen(null)
    }

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
            onLocationDelete = viewModel::deleteLocation,
            onLocationAsHomeSet = viewModel::setLocationHomeStatus,
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
    onLocationDelete: (Long) -> Unit,
    onLocationAsHomeSet: (Long) -> Unit,
    itemBackgroundColor: Color,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(
            items = locationItems,
            key = { _, item -> item.id }
        ) { index, item ->
            SwipeToRevealLocationItem(
                modifier = Modifier.animateItem(),
                itemHeight = 75.dp,
                locationItem = item,
                onLocationItemClick = onLocationItemClick,
                onLocationDelete = onLocationDelete,
                onLocationAsHomeSet = onLocationAsHomeSet,
                itemBackgroundColor = itemBackgroundColor
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
            text = stringResource(R.string.add_location),
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