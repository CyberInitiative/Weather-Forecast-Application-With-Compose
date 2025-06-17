package com.example.weathercompose.ui.compose.main_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.ui.compose.forecast_screen.ForecastScreen
import com.example.weathercompose.ui.compose.location_manager_screen.LocationManagerContent
import com.example.weathercompose.ui.compose.location_search_screen.LocationSearchScreen
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
import com.example.weathercompose.ui.navigation.NavigationRoute
import com.example.weathercompose.ui.viewmodel.ForecastViewModel
import org.koin.androidx.compose.koinViewModel

const val LOCATION_ID = "location_id"

@Composable
fun NavigationHost(
    navController: NavHostController,
    forecastViewModel: ForecastViewModel,
    weatherAndDayTimeState: WeatherAndDayTimeState,
    onPrecipitationConditionChange: (WeatherAndDayTimeState) -> Unit,
    onLocationNameSet: (String) -> Unit,
    onLocationNameVisibilityChange: (Boolean) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Forecast,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable<NavigationRoute.Forecast> { backStackEntry ->
            val locationId: Long? = backStackEntry.savedStateHandle.get<Long>(LOCATION_ID)

            val onNavigateToLocationSearchScreen = {
                navController.navigate(
                    route = NavigationRoute.LocationSearch(
                        isLocationsEmpty = forecastViewModel.isLocationsEmpty()
                    )
                )
            }

            ForecastScreen(
                viewModel = forecastViewModel,
                onAppearanceStateChange = onPrecipitationConditionChange,
                onNavigateToLocationSearchScreen = onNavigateToLocationSearchScreen,
                locationId = locationId,
                onLocationNameSet = onLocationNameSet,
                onLocationNameVisibilityChange = onLocationNameVisibilityChange,
            )
        }

        composable<NavigationRoute.LocationsManager> {
            val onNavigateToForecastScreen: (Long?) -> Unit = { locationId: Long? ->
                navController.previousBackStackEntry?.savedStateHandle?.set(LOCATION_ID, locationId)
                navController.popBackStack()
            }

            val onNavigateToSearchScreen = {
                navController.navigate(
                    route = NavigationRoute.LocationSearch(isLocationsEmpty = false)
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

            val onNavigateToForecastScreen: (LocationEntity) -> Unit = { location: LocationEntity ->
                val forecastBackStackEntry =
                    navController.getBackStackEntry(NavigationRoute.Forecast)
                forecastBackStackEntry.savedStateHandle[LOCATION_ID] = location.locationId
                navController.popBackStack(NavigationRoute.Forecast, inclusive = false)
            }

            LocationSearchScreen(
                viewModel = koinViewModel(),
                forecastViewModel = forecastViewModel,
                weatherAndDayTimeState = weatherAndDayTimeState,
                isLocationsEmpty = locationSearch.isLocationsEmpty,
                onNavigateToForecastScreen = onNavigateToForecastScreen
            )
        }
    }
}