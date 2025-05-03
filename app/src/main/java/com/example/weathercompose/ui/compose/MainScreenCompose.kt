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
import com.example.weathercompose.R
import com.example.weathercompose.ui.compose.forecast_screen.ForecastContent
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.navigation.NavigationRoute
import com.example.weathercompose.ui.viewmodel.ForecastViewModel
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainScreenCompose"

private const val COLOR_TRANSITION_ANIMATION_DURATION: Int = 700

@Composable
fun NavigationHost(
    navController: NavHostController,
    precipitationCondition: PrecipitationCondition,
    onPrecipitationConditionChange: (PrecipitationCondition) -> Unit,
) {
    val forecastViewModel: ForecastViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Forecast,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable<NavigationRoute.Forecast> {
            val onNavigateToLocationSearchScreen = {
                navController.navigate(NavigationRoute.LocationSearch)
            }

            ForecastContent(
                viewModel = forecastViewModel,
                onAppearanceStateChange = onPrecipitationConditionChange,
                onNavigateToLocationSearchScreen = onNavigateToLocationSearchScreen,
            )

        }

        composable<NavigationRoute.LocationsManager> {
            val onNavigateToForecastScreen = { locationId: Long ->
                forecastViewModel.setCurrentLocationForecast(locationId = locationId)
                navController.navigate(NavigationRoute.Forecast) {
                    popUpTo<NavigationRoute.Forecast> {
                        inclusive = true
                    }
                }
            }

            LocationManagerContent(
                viewModel = forecastViewModel,
                precipitationCondition = precipitationCondition,
                onNavigateToSearchScreen = { navController.navigate(NavigationRoute.LocationSearch) },
                onNavigateToForecastScreen = onNavigateToForecastScreen
            )
        }

        composable<NavigationRoute.LocationSearch> {
            val onNavigateToForecastScreen = {
                navController.navigate(NavigationRoute.Forecast) {
                    popUpTo<NavigationRoute.Forecast> {
                        inclusive = true
                    }
                }
            }

            LocationSearchContent(
                viewModel = forecastViewModel,
                precipitationCondition = precipitationCondition,
                onNavigateToForecastScreen = onNavigateToForecastScreen
            )
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    var precipitationCondition by rememberSaveable {
        mutableStateOf(PrecipitationCondition.NO_PRECIPITATION_DAY)
    }
    val onAppearanceStateChange = { state: PrecipitationCondition ->
        precipitationCondition = state
    }

    val context = LocalContext.current
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

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colorStops = colorStops))
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                MainScreenTopAppBar(
                    navController = navController,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                NavigationHost(
                    navController = navController,
                    precipitationCondition = precipitationCondition,
                    onPrecipitationConditionChange = onAppearanceStateChange
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenTopAppBar(
    navController: NavController,
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
        title = { },
        actions = {
            when {
                destination?.hasRoute<NavigationRoute.Forecast>() == true -> {
                    TopAppBarOptionsMenu(
                        menuExpanded = menuExpanded,
                        onMenuClick = onMenuClick,
                        closeMenu = onMenuDismiss,
                        navigateLocationsManagerScreen = navigateLocationsManagerScreen
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
) {
    IconButton(onClick = onMenuClick) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "More options",
            tint = Color.White
        )
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { closeMenu() }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Manage locations",
                        fontSize = 16.sp,
                    )
                },
                onClick = {
                    navigateLocationsManagerScreen()
                    closeMenu()
                }
            )
            OptionsMenuItemDivider()
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Temperature Unit",
                        fontSize = 16.sp,
                    )
                },
                onClick = { /* Do something... */ }
            )
            OptionsMenuItemDivider()
            DropdownMenuItem(
                text = {
                    Text(
                        "Update frequency",
                        fontSize = 16.sp,
                    )
                },
                onClick = { /* Do something... */ }
            )
        }
    }
}

@Composable
private fun OptionsMenuItemDivider() {
    HorizontalDivider(
        thickness = 0.8.dp,
        color = colorResource(R.color.intercoastal_gray)
    )
}

/*
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.4f to colorResource(id = R.color.castle_moat),
                            1f to colorResource(id = R.color.skysail_blue)
                        )
                    )
 */

/*
LaunchedEffect(savedLocationId) {
                if (savedLocationId != null) {
                    forecastViewModel.setCurrentLocationForecast(locationId = savedLocationId)
                    backStackEntry.savedStateHandle.remove<Long>(SAVED_LOCATION_ID_KEY)
                }
            }
 */