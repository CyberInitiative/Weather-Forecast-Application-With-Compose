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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathercompose.R
import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.ui_state.LocationSearchState
import com.example.weathercompose.ui.viewmodel.LocationSearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "LocationsManagerCompose"

@Composable
fun LocationSearchScreen(
    viewModel: LocationSearchViewModel,
    precipitationCondition: PrecipitationCondition,
    isLocationsEmpty: Boolean,
    onNavigateToForecastScreen: (LocationEntity) -> Any,
) {
    val activity = LocalContext.current as? Activity
    val locationSearchResult by viewModel.locationSearchState.collectAsState()

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var listItemsColor by remember { mutableIntStateOf(R.color.liberty) }

    val onQueryChanged = { newQuery: String ->
        query = newQuery
        viewModel.searchLocation(newQuery)
    }

    val onLocationItemClick = { location: LocationEntity ->
        coroutineScope.launch {
            onNavigateToForecastScreen(location)
        }
    }

    if (isLocationsEmpty) {
        BackHandler {
            activity?.finish()
        }
    }

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

    LocationSearchContent(
        query = query,
        onQueryChanged = onQueryChanged,
        onLocationItemClick = onLocationItemClick,
        locationSearchResult = locationSearchResult,
        focusManager = focusManager,
        listItemsColor = listItemsColor,
    )
}

@Composable
private fun LocationSearchContent(
    query: String,
    onQueryChanged: (String) -> Unit,
    onLocationItemClick: (LocationEntity) -> Job,
    locationSearchResult: LocationSearchState,
    focusManager: FocusManager,
    @ColorRes
    listItemsColor: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SearchBox(
            query = query,
            onQueryChanged = onQueryChanged,
            itemBackgroundColor = listItemsColor,
            focusManager = focusManager,
        )

        when {
            locationSearchResult.isLoading -> LoadingProcessIndicator()
            locationSearchResult.locations.isNotEmpty() ->
                LocationList(
                    locations = locationSearchResult.locations,
                    itemBackgroundColor = listItemsColor,
                    onLocationItemClick = onLocationItemClick,
                )
        }
    }
}

@Composable
private fun SearchBox(
    query: String,
    onQueryChanged: (String) -> Unit,
    @ColorRes itemBackgroundColor: Int,
    focusManager: FocusManager,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 15.dp, bottom = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .size(width = 230.dp, height = 45.dp)
                .background(
                    color = colorResource(itemBackgroundColor),
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(start = 10.dp, end = 15.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
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
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.fillMaxWidth(),
                cursorBrush = SolidColor(Color.White),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Search...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colorResource(R.color.bridal_veil),
                                fontSize = 18.sp
                            )
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
private fun LocationList(
    locations: List<LocationEntity>,
    modifier: Modifier = Modifier,
    @ColorRes itemBackgroundColor: Int,
    onLocationItemClick: (LocationEntity) -> Job,
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
                    thickness = 1.3.dp,
                    color = colorResource(itemBackgroundColor)
                )
            }
        }
    }
}

@Composable
private fun LocationListItem(
    location: LocationEntity,
    onLocationItemClick: (LocationEntity) -> Job,
) {
    Row(
        modifier = Modifier
            .clickable {
                onLocationItemClick(location)
            }
            .fillMaxWidth()
            .padding(15.dp)
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