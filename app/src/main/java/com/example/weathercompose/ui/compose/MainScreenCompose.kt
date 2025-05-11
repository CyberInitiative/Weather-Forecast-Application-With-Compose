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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.ui.compose.dialog.TemperatureDialog
import com.example.weathercompose.ui.compose.forecast_screen.ForecastScreen
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
import com.example.weathercompose.ui.navigation.NavigationRoute
import com.example.weathercompose.ui.theme.Bayou
import com.example.weathercompose.ui.theme.CastleMoat
import com.example.weathercompose.ui.theme.CoalBlack
import com.example.weathercompose.ui.theme.Fashionista20PerDarker
import com.example.weathercompose.ui.theme.HiloBay
import com.example.weathercompose.ui.theme.IntercoastalGray
import com.example.weathercompose.ui.theme.LimoScene35PerDarker
import com.example.weathercompose.ui.theme.VeryDarkShadeCyanBlue
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
    weatherAndDayTimeState: WeatherAndDayTimeState,
    onPrecipitationConditionChange: (WeatherAndDayTimeState) -> Unit,
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
                weatherAndDayTimeState = weatherAndDayTimeState,
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
                weatherAndDayTimeState = weatherAndDayTimeState,
                isLocationsEmpty = locationSearch.isLocationsEmpty,
                onNavigateToForecastScreen = onNavigateToForecastScreen
            )
        }
    }
}

@Composable
fun MainScreen() {
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()

    val forecastViewModel: ForecastViewModel = koinViewModel()

    var weatherAndDayTimeState by rememberSaveable {
        mutableStateOf(WeatherAndDayTimeState.NO_PRECIPITATION_DAY)
    }

    val onAppearanceStateChange = { state: WeatherAndDayTimeState ->
        weatherAndDayTimeState = state
    }

    ScreenContent(
        coroutineScope = coroutineScope,
        navController = navController,
        forecastViewModel = forecastViewModel,
        weatherAndDayTimeState = weatherAndDayTimeState,
        onPrecipitationConditionChange = onAppearanceStateChange,
    )
}

@Composable
private fun ScreenContent(
    coroutineScope: CoroutineScope,
    navController: NavHostController,
    forecastViewModel: ForecastViewModel,
    weatherAndDayTimeState: WeatherAndDayTimeState,
    onPrecipitationConditionChange: (WeatherAndDayTimeState) -> Unit,
) {
    AnimatedStatefulGradientBackground(
        weatherAndDayTimeState = weatherAndDayTimeState,
        modifier = Modifier.fillMaxSize(),
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
                    weatherAndDayTimeState = weatherAndDayTimeState,
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
        color = IntercoastalGray
    )
}

@Composable
fun AnimatedStatefulGradientBackground(
    weatherAndDayTimeState: WeatherAndDayTimeState,
    modifier: Modifier = Modifier,
    durationMillis: Int = COLOR_TRANSITION_ANIMATION_DURATION,
    content: @Composable () -> Unit
) {
    val targetColors = getGradientForType(weatherAndDayTimeState)

    val topColor by animateColorAsState(
        targetValue = targetColors[0],
        animationSpec = tween(durationMillis = durationMillis),
        label = "gradientColor1"
    )

    val bottomColor by animateColorAsState(
        targetValue = targetColors[1],
        animationSpec = tween(durationMillis = durationMillis),
        label = "gradientColor2"
    )

    val colorStops = arrayOf(
        0.5f to topColor,
        0.8f to bottomColor,
    )

    val brush = Brush.verticalGradient(
        colorStops = colorStops,
    )

    Box(
        modifier = modifier
            .background(brush)
    ) {
        content()
    }
}

private fun getGradientForType(weatherAndDayTimeState: WeatherAndDayTimeState): List<Color> {
    return when (weatherAndDayTimeState) {
        WeatherAndDayTimeState.NO_PRECIPITATION_DAY -> listOf(
            CastleMoat, Bayou
        )

        WeatherAndDayTimeState.NO_PRECIPITATION_NIGHT -> listOf(
            VeryDarkShadeCyanBlue, CoalBlack
        )

        WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_DAY -> listOf(
            HiloBay, HiloBay
        )

        WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_NIGHT -> listOf(
            LimoScene35PerDarker, Fashionista20PerDarker
        )
    }
}