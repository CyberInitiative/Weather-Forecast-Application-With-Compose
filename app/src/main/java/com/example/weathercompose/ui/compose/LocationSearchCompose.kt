package com.example.weathercompose.ui.compose

import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathercompose.R
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.ui.UIState
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.viewmodel.LocationSearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "LocationsManagerCompose"

@Composable
fun LocationSearchContent(
    viewModel: LocationSearchViewModel,
    precipitationCondition: PrecipitationCondition,
    onNavigateToForecastScreen: (LocationDomainModel) -> Any,
) {
    var query by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val locationSearchResult by viewModel.locationSearchResult.collectAsState(initial = UIState.Loading())
    val onLocationItemClick = { location: LocationDomainModel ->
        coroutineScope.launch {
            viewModel.saveLocation(location).join()
            viewModel.loadForecastForLocation(location).join()

            onNavigateToForecastScreen(location)
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
            itemBackgroundColor = listItemsColor
        )

        when (locationSearchResult) {
            is UIState.Content -> {
                LocationList(
                    locations = (locationSearchResult as UIState.Content<List<LocationDomainModel>>).data,
                    onLocationItemClick = onLocationItemClick,
                )
            }

            is UIState.Empty -> {}

            is UIState.Error -> {}

            is UIState.Loading -> {
                LoadingProcessIndicator()
            }
        }
    }
}

@Composable
fun SearchBox(
    query: String,
    onQueryChanged: (String) -> Unit,
    @ColorRes
    itemBackgroundColor: Int,
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier.size(50.dp)
        )
    }
}