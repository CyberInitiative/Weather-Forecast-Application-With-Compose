package com.example.weathercompose.ui.compose.main_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.example.weathercompose.R
import com.example.weathercompose.ui.navigation.NavigationRoute
import com.example.weathercompose.ui.theme.IntercoastalGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreenTopAppBar(
    destination: NavDestination?,
    onTemperatureUnitOptionClick: () -> Unit,
    onForecastUpdateFrequencyOptionClick: () -> Unit,
    onNavigateToLocationsManagerScreen: () -> Unit,
    locationName: String,
    isLocationNameVisible: Boolean,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    val onMenuClick = { isMenuExpanded = !isMenuExpanded }
    val onMenuDismiss = { isMenuExpanded = false }

    TopAppBar(
        title = {
            ForecastScreenTopAppBarTitle(
                destination = destination,
                locationName = locationName,
                isLocationNameVisible = isLocationNameVisible,
            )
        },
        actions = {
            ForecastScreenTopAppBarActions(
                isMenuExpanded = isMenuExpanded,
                onMenuClick = onMenuClick,
                onMenuDismiss = onMenuDismiss,
                onNavigateToLocationsManagerScreen = onNavigateToLocationsManagerScreen,
                onTemperatureUnitOptionClick = onTemperatureUnitOptionClick,
                onForecastUpdateFrequencyOptionClick = onForecastUpdateFrequencyOptionClick,
                destination = destination
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
        ),
    )
}

@Composable
private fun ForecastScreenTopAppBarTitle(
    destination: NavDestination?,
    locationName: String,
    isLocationNameVisible: Boolean,
) {
    when {
        destination?.hasRoute<NavigationRoute.Forecast>() == true -> {
            AnimatedVisibility(
                visible = !isLocationNameVisible,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Text(
                    text = locationName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    color = Color.White,
                    textAlign = TextAlign.Start,
                )
            }
        }

        destination?.hasRoute<NavigationRoute.LocationsManager>() == true -> {
            Text(
                text = stringResource(R.string.manage_locations),
                modifier = Modifier.padding(start = 10.dp),
                color = Color.White,
                fontSize = 20.sp,
            )
        }

        destination?.hasRoute<NavigationRoute.LocationSearch>() == true -> {
            Text(
                text = stringResource(R.string.add_location),
                modifier = Modifier.padding(start = 10.dp),
                color = Color.White,
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
fun ForecastScreenTopAppBarActions(
    isMenuExpanded: Boolean,
    onMenuClick: () -> Unit,
    onMenuDismiss: () -> Unit,
    onNavigateToLocationsManagerScreen: () -> Unit,
    onTemperatureUnitOptionClick: () -> Unit,
    onForecastUpdateFrequencyOptionClick: () -> Unit,
    destination: NavDestination?
) {
    when {
        destination?.hasRoute<NavigationRoute.Forecast>() == true -> {
            ForecastScreenTopAppBarOptionsMenu(
                isMenuExpanded = isMenuExpanded,
                onMenuClick = onMenuClick,
                closeMenu = onMenuDismiss,
                navigateLocationsManagerScreen = onNavigateToLocationsManagerScreen,
                onTemperatureUnitOptionClick = onTemperatureUnitOptionClick,
                onForecastUpdateFrequencyOptionClick = onForecastUpdateFrequencyOptionClick,
            )
        }

        else -> Unit
    }
}

@Composable
private fun ForecastScreenTopAppBarOptionsMenu(
    isMenuExpanded: Boolean,
    onMenuClick: () -> Unit,
    closeMenu: () -> Unit,
    navigateLocationsManagerScreen: () -> Unit,
    onTemperatureUnitOptionClick: () -> Unit,
    onForecastUpdateFrequencyOptionClick: () -> Unit
) {
    IconButton(onClick = onMenuClick) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "More options",
            tint = Color.White
        )
    }

    DropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = { closeMenu() }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.manage_locations), fontSize = 16.sp) },
            onClick = {
                closeMenu()
                navigateLocationsManagerScreen()
            }
        )
        OptionsMenuItemDivider()
        DropdownMenuItem(
            text = { Text(stringResource(R.string.temperature_unit), fontSize = 16.sp) },
            onClick = {
                closeMenu()
                onTemperatureUnitOptionClick()
            }
        )
        OptionsMenuItemDivider()
        DropdownMenuItem(
            text = { Text(stringResource(R.string.update_frequency), fontSize = 16.sp) },
            onClick = {
                closeMenu()
                onForecastUpdateFrequencyOptionClick()
            }
        )
    }
}

@Composable
private fun OptionsMenuItemDivider() {
    HorizontalDivider(
        thickness = 0.8.dp,
        color = IntercoastalGray
    )
}