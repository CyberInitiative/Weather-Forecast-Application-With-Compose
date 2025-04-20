package com.example.weathercompose.ui.compose

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathercompose.R
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.ui_state.LocationForecastState
import com.example.weathercompose.ui.viewmodel.ForecastViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "LocationsManagerCompose"

@Composable
fun LocationSearchContent(
    viewModel: ForecastViewModel,
    precipitationCondition: PrecipitationCondition,
    onNavigateToForecastScreen: () -> Any,
) {
    val activity = LocalContext.current as? Activity
    val locationForecastUIState by viewModel.locationForecastState.collectAsState()
    if(locationForecastUIState == LocationForecastState.NoLocationDataForecastState){
        BackHandler {
            activity?.finish()
        }
    }

    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val locationSearchResult by viewModel.locationSearchState.collectAsState()
    val onLocationItemClick = { location: LocationDomainModel ->
        coroutineScope.launch {
            onNavigateToForecastScreen()
            viewModel.saveLocation(location = location)
        }
    }

    var listItemsColor by remember { mutableIntStateOf(R.color.liberty) }
    when (precipitationCondition) {
        PrecipitationCondition.NO_PRECIPITATION_DAY -> {
            listItemsColor = R.color.liberty
        }

        PrecipitationCondition.NO_PRECIPITATION_NIGHT -> {
            listItemsColor = R.color.mesmerize
        }

        PrecipitationCondition.PRECIPITATION_DAY -> {
            listItemsColor = R.color.hilo_bay_25_percent_darker
        }

        PrecipitationCondition.PRECIPITATION_NIGHT -> {
            listItemsColor = R.color.english_channel_10_percent_darker
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SearchBox(
            query = query,
            onQueryChanged = { newQuery ->
                query = newQuery
                coroutineScope.launch {
                    viewModel.searchLocation(name = newQuery)
                }
            },
            itemBackgroundColor = listItemsColor,
            focusManager = focusManager,
        )

        when {
            locationSearchResult.isLoading -> LoadingProcessIndicator()
            locationSearchResult.locations.isNotEmpty() ->
                LocationList(
                    locations = locationSearchResult.locations,
                    onLocationItemClick = onLocationItemClick,
                )
        }
    }
}

@Composable
fun SearchBox(
    query: String,
    onQueryChanged: (String) -> Unit,
    @ColorRes
    itemBackgroundColor: Int,
    focusManager: FocusManager,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 30.dp, bottom = 15.dp)
    ) {
        BasicTextField(
            modifier = Modifier
                .size(width = 225.dp, height = 40.dp)
                .background(
                    color = colorResource(itemBackgroundColor),
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(top = 10.dp, start = 15.dp, end = 15.dp),
            value = query,
            onValueChange = onQueryChanged,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontSize = 18.sp
            ),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
        )
    }
}

@Composable
fun LocationList(
    locations: List<LocationDomainModel>,
    modifier: Modifier = Modifier,
    onLocationItemClick: (LocationDomainModel) -> Job,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp, start = 15.dp, end = 15.dp)
    ) {
        itemsIndexed(locations) { index, location ->
            LocationListItem(
                location = location,
                onLocationItemClick = onLocationItemClick,
            )
            if (index < locations.size - 1) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = colorResource(R.color.castle_moat)
                )
            }
        }
    }
}

@Composable
fun LocationListItem(
    location: LocationDomainModel,
    onLocationItemClick: (LocationDomainModel) -> Job,
) {
    Row(
        modifier = Modifier
            .padding(15.dp)
            .clickable {
                onLocationItemClick(location)
            }
    ) {
        Text(
            color = Color.White,
            text = location.getFullLocationName(),
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun LoadingProcessIndicator() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            color = Color.White,
            text = "Loading...",
            fontSize = 16.sp,
        )
    }
}

/*
            viewModel.setLocationSearchUIStateLoading()

            val locationWithLoadedForecast = viewModel.loadForecastForLocation(location)
            viewModel.addLocation(locationWithLoadedForecast)


 */