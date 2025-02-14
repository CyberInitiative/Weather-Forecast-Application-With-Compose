package com.example.weathercompose.ui.compose

import androidx.compose.foundation.background
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathercompose.R
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.ui.model.CityItem
import com.example.weathercompose.ui.navigation.NavigationRoutes
import com.example.weathercompose.ui.viewmodel.CityManagerViewModel
import com.example.weathercompose.ui.viewmodel.CitySearchViewModel
import com.example.weathercompose.ui.viewmodel.MainViewModel
import com.example.weathercompose.ui.viewmodel.SharedViewModel
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainScreenCompose"
const val SAVED_CITY_ID_KEY = "saved_city_id_key"

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Forecast,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<NavigationRoutes.Forecast> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(NavigationRoutes.Forecast)
            }

            val viewModel = koinViewModel<MainViewModel>()
            val sharedViewModel = koinViewModel<SharedViewModel>(viewModelStoreOwner = parentEntry)

            val savedCityId = backStackEntry.savedStateHandle.get<Long>(SAVED_CITY_ID_KEY)
            LaunchedEffect(savedCityId) {
                if (savedCityId != null) {
                    sharedViewModel.loadCity(savedCityId)
                    viewModel.setCurrentCityId(cityId = savedCityId)
                }
            }
            ForecastContent(
                viewModel = viewModel,
                sharedViewModel = sharedViewModel,
            )
        }

        composable<NavigationRoutes.CitiesManager> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(NavigationRoutes.Forecast)
            }

            val sharedViewModel = koinViewModel<SharedViewModel>(viewModelStoreOwner = parentEntry)

            CityManagerContent(
                viewModel = koinViewModel<CityManagerViewModel>(),
                sharedViewModel = sharedViewModel,
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
                onNavigateToForecastScreen = onNavigateToForecastScreen
            )
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MainScreenTopAppBar(navController = navController)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.4f to colorResource(id = R.color.castle_moat),
                            1f to colorResource(id = R.color.skysail_blue)
                        )
                    )
                )
        ) {
            NavigationHost(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenTopAppBar(navController: NavController) {
    var menuExpanded by remember { mutableStateOf(false) }

    val onMenuClick = { menuExpanded = !menuExpanded }
    val onMenuDismiss = { menuExpanded = false }

    val navigateManageCitiesScreen =
        { navController.navigate(NavigationRoutes.CitiesManager) }

    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = colorResource(R.color.castle_moat),
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