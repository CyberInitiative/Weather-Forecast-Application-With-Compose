package com.example.weathercompose.ui.compose

import androidx.compose.animation.AnimatedContentTransitionScope
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathercompose.R
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.ui.model.CityItem
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.navigation.NavigationRoutes
import com.example.weathercompose.ui.viewmodel.CityManagerViewModel
import com.example.weathercompose.ui.viewmodel.CitySearchViewModel
import com.example.weathercompose.ui.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainScreenCompose"

const val SAVED_CITY_ID_KEY = "saved_city_id_key"

private const val SCREEN_TRANSITION_ANIMATION_DURATION: Int = 500
private const val COLOR_TRANSITION_ANIMATION_DURATION: Int = 700

@Composable
fun NavigationHost(
    navController: NavHostController,
    precipitationCondition: PrecipitationCondition,
    onPrecipitationConditionChange: (PrecipitationCondition) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Forecast,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(SCREEN_TRANSITION_ANIMATION_DURATION)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(SCREEN_TRANSITION_ANIMATION_DURATION)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(SCREEN_TRANSITION_ANIMATION_DURATION)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(SCREEN_TRANSITION_ANIMATION_DURATION)
            )
        }
    ) {
        composable<NavigationRoutes.Forecast> { backStackEntry ->
            val viewModel = koinViewModel<MainViewModel>()

            val savedCityId = backStackEntry.savedStateHandle.get<Long>(SAVED_CITY_ID_KEY)

            LaunchedEffect(savedCityId) {
                if (savedCityId != null) {
                    viewModel.setCurrentCityForecast(cityId = savedCityId)
                }
            }

            ForecastContent(
                viewModel = viewModel,
                onAppearanceStateChange = onPrecipitationConditionChange,
            )
        }

        composable<NavigationRoutes.CitiesManager> {
            CityManagerContent(
                viewModel = koinViewModel<CityManagerViewModel>(),
                precipitationCondition = precipitationCondition,
                onNavigateToSearchScreen = { navController.navigate(NavigationRoutes.CitySearch) },
                onNavigateToForecastScreen = { cityItem: CityItem ->
                    val previousBackStackEntry = navController.previousBackStackEntry
                    val savedStateHandle = previousBackStackEntry?.savedStateHandle
                    savedStateHandle?.set(SAVED_CITY_ID_KEY, cityItem.id)
                    navController.popBackStack()
                }
            )
        }

        composable<NavigationRoutes.CitySearch> {
            val viewModel = koinViewModel<CitySearchViewModel>()

            val onNavigateToForecastScreen = { city: CityDomainModel ->
                val savedStateHandle =
                    navController.getBackStackEntry<NavigationRoutes.Forecast>().savedStateHandle
                savedStateHandle[SAVED_CITY_ID_KEY] = city.id
                navController.popBackStack<NavigationRoutes.Forecast>(inclusive = false)
            }

            CitiesSearchContent(
                viewModel = viewModel,
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
    val backgroundColor by animateColorAsState(
        targetValue = when (precipitationCondition) {
            PrecipitationCondition.NO_PRECIPITATION_DAY -> {
                Color(ContextCompat.getColor(context, R.color.castle_moat))
            }

            PrecipitationCondition.NO_PRECIPITATION_NIGHT -> {
                Color(ContextCompat.getColor(context, R.color.peaceful_night))
            }

            PrecipitationCondition.PRECIPITATION_DAY -> {
                Color(ContextCompat.getColor(context, R.color.hilo_bay))
            }

            PrecipitationCondition.PRECIPITATION_NIGHT -> {
                Color(ContextCompat.getColor(context, R.color.english_channel_45_percent_darker))
            }

        },
        animationSpec = tween(durationMillis = COLOR_TRANSITION_ANIMATION_DURATION),
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
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

    val navigateManageCitiesScreen =
        { navController.navigate(NavigationRoutes.CitiesManager) }

    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = Color.White,
    ), title = {
        Text("Weather Forecast")
    }, actions = {
        TopAppBarOptionsMenu(
            menuExpanded = menuExpanded,
            onMenuClick = onMenuClick,
            closeMenu = onMenuDismiss,
            navigateManageCitiesScreen = navigateManageCitiesScreen
        )
    })
}

@Composable
private fun TopAppBarOptionsMenu(
    menuExpanded: Boolean,
    onMenuClick: () -> Unit,
    closeMenu: () -> Unit,
    navigateManageCitiesScreen: () -> Unit,
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
                        text = "Manage cities",
                        fontSize = 16.sp,
                    )
                },
                onClick = {
                    navigateManageCitiesScreen()
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