package com.example.weathercompose.ui.compose

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathercompose.R
import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.ui.compose.dialog.NoInternetDialog
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
import com.example.weathercompose.ui.theme.HiloBay25PerDarker
import com.example.weathercompose.ui.theme.Liberty
import com.example.weathercompose.ui.theme.MediumDarkShadeCyanBlue
import com.example.weathercompose.ui.theme.SiberianIce
import com.example.weathercompose.ui.theme.Solitaire5PerDarker
import com.example.weathercompose.ui.ui_state.LocationSearchState
import com.example.weathercompose.ui.viewmodel.LocationSearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "LocationsManagerCompose"

@Composable
fun LocationSearchScreen(
    viewModel: LocationSearchViewModel,
    weatherAndDayTimeState: WeatherAndDayTimeState,
    isLocationsEmpty: Boolean,
    onNavigateToForecastScreen: (LocationEntity) -> Any,
) {
    val context = LocalContext.current
    val locationSearchResult by viewModel.locationSearchState.collectAsState()

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var query by rememberSaveable { mutableStateOf("") }
    var isDebouncing by remember { mutableStateOf(false) }
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    var isNoInternetDialogVisible by rememberSaveable { mutableStateOf(false) }

    if (isNoInternetDialogVisible) {
        NoInternetDialog(
            onDismiss = { isNoInternetDialogVisible = false },
            onConfirm = { context.startActivity(Intent(Settings.ACTION_SETTINGS)) },
            weatherAndDayTimeState = weatherAndDayTimeState
        )
    }

    val uiElementsColor by remember(weatherAndDayTimeState) {
        mutableStateOf(
            when (weatherAndDayTimeState) {
                WeatherAndDayTimeState.NO_PRECIPITATION_DAY -> Liberty
                WeatherAndDayTimeState.NO_PRECIPITATION_NIGHT -> MediumDarkShadeCyanBlue
                WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_DAY -> HiloBay25PerDarker
                WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_NIGHT -> Solitaire5PerDarker
            }
        )
    }

    val onQueryChanged: (String) -> Unit = { newQuery: String ->
        query = newQuery
        if (!viewModel.isNetworkAvailable()) {
            isNoInternetDialogVisible = true
            viewModel.clearSearchResult()
            debounceJob?.cancel()
        } else {
            isDebouncing = true
            debounceJob?.cancel()
            debounceJob = coroutineScope.launch {
                delay(300)
                isDebouncing = false
                viewModel.searchLocation(newQuery)
            }
        }
    }

    val onLocationItemClick = { location: LocationEntity ->
        coroutineScope.launch {
            onNavigateToForecastScreen(location)
        }
    }

    if (isLocationsEmpty) {
        BackHandler {
            (context as? Activity)?.finish()
        }
    }

    LocationSearchContent(
        query = query,
        onQueryChanged = onQueryChanged,
        isDebouncing = isDebouncing,
        onLocationItemClick = onLocationItemClick,
        locationSearchResult = locationSearchResult,
        focusManager = focusManager,
        uiElementsColor = uiElementsColor,
    )
}

@Composable
private fun LocationSearchContent(
    query: String,
    onQueryChanged: (String) -> Unit,
    isDebouncing: Boolean,
    onLocationItemClick: (LocationEntity) -> Job,
    locationSearchResult: LocationSearchState,
    focusManager: FocusManager,
    uiElementsColor: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SearchBox(
            query = query,
            onQueryChanged = onQueryChanged,
            uiElementsColor = uiElementsColor,
            focusManager = focusManager,
        )

        when {
            locationSearchResult.isLoading || isDebouncing -> LoadingProcessIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .imePadding()
            )

            locationSearchResult.locations.isNotEmpty() ->
                LocationList(
                    locations = locationSearchResult.locations,
                    onLocationItemClick = onLocationItemClick,
                )

            locationSearchResult.locations.isEmpty() && query.isNotBlank() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        modifier = Modifier.padding(bottom = 10.dp),
                        painter = painterResource(R.drawable.location_icon_2),
                        contentDescription = "No locations found icon",
                        tint = Color.White,
                    )

                    Text(
                        text = stringResource(R.string.no_locations_found_search_result),
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBox(
    query: String,
    onQueryChanged: (String) -> Unit,
    uiElementsColor: Color,
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
                    color = uiElementsColor,
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
                        SearchBoxHint()
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
                    color = Color.White
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
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SearchBoxHint() {
    Text(
        text = stringResource(R.string.location_search_box_hint),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = SiberianIce,
            fontSize = 18.sp
        )
    )
}

@Composable
fun LoadingProcessIndicator(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
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
            text = stringResource(R.string.loading_process_text),
            fontSize = 16.sp,
        )
    }
}