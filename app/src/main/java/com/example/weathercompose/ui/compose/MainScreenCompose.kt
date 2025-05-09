package com.example.weathercompose.ui.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weathercompose.R
import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.ui.compose.forecast_screen.ForecastScreen
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.navigation.NavigationRoute
import com.example.weathercompose.ui.viewmodel.ForecastViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainScreenCompose"

private const val COLOR_TRANSITION_ANIMATION_DURATION: Int = 700

@Composable
fun NavigationHost(
    coroutineScope: CoroutineScope,
    navController: NavHostController,
    forecastViewModel: ForecastViewModel,
    precipitationCondition: PrecipitationCondition,
    onPrecipitationConditionChange: (PrecipitationCondition) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Forecast(locationId = null),
        modifier = Modifier.fillMaxSize(),
    ) {
        composable<NavigationRoute.Forecast> { backStackEntry ->
            val forecast: NavigationRoute.Forecast = backStackEntry.toRoute()

            val onNavigateToLocationSearchScreen = {
                navController.navigate(
                    NavigationRoute.LocationSearch(
                        isLocationsEmpty = forecastViewModel.isLocationsEmpty()
                    )
                )
            }

            ForecastScreen(
                viewModel = forecastViewModel,
                onAppearanceStateChange = onPrecipitationConditionChange,
                onNavigateToLocationSearchScreen = onNavigateToLocationSearchScreen,
                locationId = forecast.locationId,
            )
        }

        composable<NavigationRoute.LocationsManager> {
            val onNavigateToForecastScreen = { locationId: Long ->
                navController.navigate(NavigationRoute.Forecast(locationId = locationId)) {
                    popUpTo<NavigationRoute.Forecast> {
                        inclusive = true
                    }
                }
            }

            val onNavigateToSearchScreen = {
                navController.navigate(
                    NavigationRoute.LocationSearch(
                        isLocationsEmpty = false
                    )
                )
            }

            LocationManagerContent(
                viewModel = forecastViewModel,
                precipitationCondition = precipitationCondition,
                onNavigateToSearchScreen = onNavigateToSearchScreen,
                onNavigateToForecastScreen = onNavigateToForecastScreen
            )
        }

        composable<NavigationRoute.LocationSearch> { backStackEntry ->
            val locationSearch: NavigationRoute.LocationSearch = backStackEntry.toRoute()

            val onNavigateToForecastScreen = { location: LocationEntity ->
                coroutineScope.launch {
                    forecastViewModel.saveLocation(locationEntity = location)
                    navController.navigate(
                        NavigationRoute.Forecast(locationId = location.locationId)
                    ) {
                        popUpTo<NavigationRoute.Forecast> {
                            inclusive = true
                        }
                    }
                }
            }

            LocationSearchScreen(
                viewModel = koinViewModel(),
                precipitationCondition = precipitationCondition,
                isLocationsEmpty = locationSearch.isLocationsEmpty,
                onNavigateToForecastScreen = onNavigateToForecastScreen
            )
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    val forecastViewModel: ForecastViewModel = koinViewModel()

    var precipitationCondition by rememberSaveable {
        mutableStateOf(PrecipitationCondition.NO_PRECIPITATION_DAY)
    }
    val onAppearanceStateChange = { state: PrecipitationCondition ->
        precipitationCondition = state
    }

    val backgroundColorTop by animateColorAsState(
        targetValue = when (precipitationCondition) {
            PrecipitationCondition.NO_PRECIPITATION_DAY -> {
                Color(ContextCompat.getColor(context, R.color.castle_moat))
            }

            PrecipitationCondition.NO_PRECIPITATION_NIGHT -> {
                Color(ContextCompat.getColor(context, R.color.chinese_black))
            }

            PrecipitationCondition.PRECIPITATION_DAY -> {
                Color(ContextCompat.getColor(context, R.color.hilo_bay))
            }

            PrecipitationCondition.PRECIPITATION_NIGHT -> {
                Color(
                    ContextCompat.getColor(
                        context,
                        R.color.english_channel_45_percent_darker
                    )
                )
            }

        },
        animationSpec = tween(durationMillis = COLOR_TRANSITION_ANIMATION_DURATION),
    )

    val backgroundColorBottom by animateColorAsState(
        targetValue = when (precipitationCondition) {
            PrecipitationCondition.NO_PRECIPITATION_DAY -> {
                Color(143, 208, 252)
            }

            PrecipitationCondition.NO_PRECIPITATION_NIGHT -> {
                Color(ContextCompat.getColor(context, R.color.peaceful_night))
            }

            PrecipitationCondition.PRECIPITATION_DAY -> {
                Color(ContextCompat.getColor(context, R.color.hilo_bay))
            }

            PrecipitationCondition.PRECIPITATION_NIGHT -> {
                Color(
                    ContextCompat.getColor(
                        context,
                        R.color.english_channel_45_percent_darker
                    )
                )
            }

        },
        animationSpec = tween(durationMillis = COLOR_TRANSITION_ANIMATION_DURATION),
    )

    val colorStops = arrayOf(
        0.5f to backgroundColorTop,
        0.9f to backgroundColorBottom,
    )
    val brush = Brush.linearGradient(colorStops = colorStops)

    ScreenContent(
        coroutineScope = coroutineScope,
        navController = navController,
        forecastViewModel = forecastViewModel,
        precipitationCondition = precipitationCondition,
        onPrecipitationConditionChange = onAppearanceStateChange,
        brush = brush
    )
}

@Composable
private fun ScreenContent(
    coroutineScope: CoroutineScope,
    navController: NavHostController,
    forecastViewModel: ForecastViewModel,
    precipitationCondition: PrecipitationCondition,
    onPrecipitationConditionChange: (PrecipitationCondition) -> Unit,
    brush: Brush,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(brush)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                MainScreenTopAppBar(
                    navController = navController,
                    onTemperatureUnitConfirm = { selectedUnit: Int ->
                        coroutineScope.launch {
                            val unit = TemperatureUnit.entries[selectedUnit];
                            forecastViewModel.setCurrentTemperatureUnit(unit)
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                NavigationHost(
                    coroutineScope = coroutineScope,
                    navController = navController,
                    forecastViewModel = forecastViewModel,
                    precipitationCondition = precipitationCondition,
                    onPrecipitationConditionChange = onPrecipitationConditionChange,
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenTopAppBar(
    navController: NavController,
    onTemperatureUnitConfirm: (Int) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val onMenuClick = { menuExpanded = !menuExpanded }
    val onMenuDismiss = { menuExpanded = false }

    val navigateLocationsManagerScreen =
        { navController.navigate(NavigationRoute.LocationsManager) }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = currentBackStackEntry?.destination

    TopAppBar(
        colors = TopAppBarDefaults
            .topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White,
            ),
        title = {
            when {
                destination?.hasRoute<NavigationRoute.LocationsManager>() == true -> {
                    Text(
                        text = "Manage locations",
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                    )
                }

                destination?.hasRoute<NavigationRoute.LocationSearch>() == true -> {
                    Text(
                        text = "Add location",
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                    )
                }
            }

        },
        actions = {
            when {
                destination?.hasRoute<NavigationRoute.Forecast>() == true -> {
                    TopAppBarOptionsMenu(
                        menuExpanded = menuExpanded,
                        onMenuClick = onMenuClick,
                        closeMenu = onMenuDismiss,
                        navigateLocationsManagerScreen = navigateLocationsManagerScreen,
                        onTemperatureUnitConfirm = onTemperatureUnitConfirm,
                    )
                }

                else -> {}
            }
        }
    )
}

@Composable
private fun TopAppBarOptionsMenu(
    menuExpanded: Boolean,
    onMenuClick: () -> Unit,
    closeMenu: () -> Unit,
    navigateLocationsManagerScreen: () -> Unit,
    onTemperatureUnitConfirm: (Int) -> Unit
) {
    var isDialogVisible by remember { mutableStateOf(false) }

    IconButton(onClick = onMenuClick) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "More options",
            tint = Color.White
        )
    }

    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = { closeMenu() }
    ) {
        DropdownMenuItem(
            text = { Text("Manage locations", fontSize = 16.sp) },
            onClick = {
                closeMenu()
                navigateLocationsManagerScreen()
            }
        )
        OptionsMenuItemDivider()
        DropdownMenuItem(
            text = { Text("Temperature Unit", fontSize = 16.sp) },
            onClick = {
                closeMenu()
                isDialogVisible = true
            }
        )
    }

    if (isDialogVisible) {
        TemperatureDialog(
            onDismiss = { isDialogVisible = false },
            onConfirm = { selectedOption ->
                isDialogVisible = false
                onTemperatureUnitConfirm(selectedOption)
            }
        )
    }
}

@Composable
private fun OptionsMenuItemDivider() {
    HorizontalDivider(
        thickness = 0.8.dp,
        color = colorResource(R.color.intercoastal_gray)
    )
}

@Composable
private fun TemperatureOptionMenu(
    onTemperatureUnitConfirm: (Int) -> Unit,
    closeMenu: () -> Unit,
) {
    var isDialogVisible by remember { mutableStateOf(false) }

    DropdownMenuItem(
        text = {
            Text(
                text = "Temperature Unit",
                fontSize = 16.sp,
            )
        },
        onClick = {
            closeMenu()
            isDialogVisible = true
        }
    )

    if (isDialogVisible) {
        TemperatureDialog(
            onDismiss = { isDialogVisible = false },
            onConfirm = { selectedOption ->
                onTemperatureUnitConfirm(selectedOption)
                isDialogVisible = false
            }
        )
    }
}