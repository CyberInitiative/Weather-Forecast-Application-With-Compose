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
import com.example.weathercompose.ui.navigation.NavigationRoutes
import com.example.weathercompose.ui.viewmodel.CityManagerViewModel
import com.example.weathercompose.ui.viewmodel.CitySearchViewModel
import com.example.weathercompose.ui.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainScreenCompose"
const val SAVED_CITY_ID_KEY = "saved_city_id_key"

@Composable
fun NavigationHost(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = NavigationRoutes.Forecast,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<NavigationRoutes.Forecast> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navHostController.getBackStackEntry(NavigationRoutes.Forecast)
            }

            //val sharedViewModel = koinViewModel<SharedViewModel>(viewModelStoreOwner = parentEntry)
//            val viewModel = koinViewModel<MainViewModel>()
            val viewModel = koinViewModel<MainViewModel>(viewModelStoreOwner = parentEntry)

            val savedCityId = backStackEntry.savedStateHandle.get<String>(SAVED_CITY_ID_KEY)
            LaunchedEffect(savedCityId) {
                if (savedCityId != null) {
//                    viewModel.loadForecastForCity(cityId = savedCityId.toInt())
                }
            }
            ForecastContent(
                viewModel = viewModel,
                //sharedViewModel = sharedViewModel,
                navController = navHostController
            )
        }

        composable<NavigationRoutes.CitiesManager> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navHostController.getBackStackEntry(NavigationRoutes.Forecast)
            }

//            val sharedViewModel = koinViewModel<SharedViewModel>(viewModelStoreOwner = parentEntry)
            val mainViewModel = koinViewModel<MainViewModel>(viewModelStoreOwner = parentEntry)

            CityListContent(
                viewModel = koinViewModel<CityManagerViewModel>(),
                mainViewModel = mainViewModel,
                navController = navHostController
            )
        }

        composable<NavigationRoutes.CitySearch> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navHostController.getBackStackEntry(NavigationRoutes.Forecast)
            }

            val mainViewModel = koinViewModel<MainViewModel>(viewModelStoreOwner = parentEntry)

            CitiesManagerContent(
                viewModel = koinViewModel<CitySearchViewModel>(),
                mainViewModel = mainViewModel,
                navController = navHostController
            )
        }
    }
}

//@Composable
//inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
//    navController: NavHostController
//): T {
//    val navGraphRoute = destination.parent?.route ?: return koinViewModel()
//
//    val parentEntry: NavBackStackEntry = remember(this) {
//        navController.getBackStackEntry(navGraphRoute)
//    }
//
//    return koinViewModel(viewModelStoreOwner = parentEntry)
//}

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
            NavigationHost(navHostController = navController)
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