package com.example.weathercompose.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.weathercompose.R
import com.example.weathercompose.ui.model.CityItem
import com.example.weathercompose.ui.viewmodel.CityManagerViewModel
import com.example.weathercompose.ui.viewmodel.SharedViewModel

@Composable
fun CityManagerContent(
    viewModel: CityManagerViewModel,
    sharedViewModel: SharedViewModel,
    onNavigateToSearchScreen: () -> Unit,
    onNavigateToForecastScreen: (CityItem) -> Unit,
) {
    val loadedCities by sharedViewModel.loadedCitiesState.collectAsState()
    val cityItems by viewModel.cityItems.collectAsState()

    LaunchedEffect(loadedCities) {
        viewModel.setLoadedCities(cities = loadedCities)
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        val (cityList, addCityButton) = createRefs()
        val cityListModifier = Modifier.constrainAs(cityList) {
            top.linkTo(parent.top)
            bottom.linkTo(addCityButton.top, margin = 20.dp)
            height = Dimension.fillToConstraints
        }
        val addCityButtonModifier = Modifier.constrainAs(addCityButton) {
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        CityList(
            cityItems = cityItems,
            modifier = cityListModifier,
            onCityItemClick = onNavigateToForecastScreen,
        )

        AddCityButton(
            onButtonClick = onNavigateToSearchScreen,
            modifier = addCityButtonModifier,
        )
    }
}

@Composable
private fun CityList(
    cityItems: List<CityItem>,
    modifier: Modifier = Modifier,
    onCityItemClick: (CityItem) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(cityItems) { index, item ->
            CityListItem(
                cityItem = item,
                onCityItemClick = onCityItemClick
            )
            if (index != cityItems.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 7.dp,
                    color = Color.Transparent
                )
            }
        }
    }
}

@Composable
private fun CityListItem(
    cityItem: CityItem,
    onCityItemClick: (CityItem) -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = colorResource(R.color.liberty),
                shape = RoundedCornerShape(15.dp)
            )
            .clickable {
                onCityItemClick(cityItem)
            },
    ) {
        val (weatherIcon, weatherDescriptionLabel, temperatureLabel, cityNameLabel) = createRefs()

        val weatherIconModifier = Modifier.constrainAs(weatherIcon) {
            top.linkTo(parent.top, margin = 10.dp)
            bottom.linkTo(parent.bottom, margin = 10.dp)
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

        val cityNameLabelModifier = Modifier.constrainAs(cityNameLabel) {
            start.linkTo(parent.start, margin = 10.dp)
            end.linkTo(weatherDescriptionLabel.start, margin = 30.dp)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
        }

        Image(
            painter = painterResource(id = cityItem.currentHourWeatherIconRes),
            contentDescription = "Current hour weather icon",
            modifier = weatherIconModifier.size(45.dp)
        )

        Text(
            text = cityItem.currentHourTemperature,
            modifier = temperatureModifier,
            color = Color.White,
            fontSize = 16.sp,
        )

        Text(
            text = stringResource(cityItem.currentHourWeatherDescription),
            modifier = weatherDescriptionModifier
                .width(100.dp),
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        Text(
            text = cityItem.name,
            modifier = cityNameLabelModifier.fillMaxWidth(),
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@Composable
private fun AddCityButton(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onButtonClick,
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        colors = ButtonColors(
            containerColor = colorResource(R.color.liberty),
            contentColor = Color.White,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.Gray,
        )
    ) {
        Text(
            text = "Add city",
            modifier = Modifier
                .padding(
                    vertical = 10.dp,
                    horizontal = 25.dp
                ),
            color = Color.White,
            fontSize = 18.sp,
        )
    }
}