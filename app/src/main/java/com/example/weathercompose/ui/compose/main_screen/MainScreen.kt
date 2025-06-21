package com.example.weathercompose.ui.compose.main_screen

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.ui.compose.dialog.ForecastUpdateFrequencyDialog
import com.example.weathercompose.ui.compose.dialog.NoInternetDialog
import com.example.weathercompose.ui.compose.dialog.TemperatureDialog
import com.example.weathercompose.ui.compose.shared.toSecondaryBackgroundColor
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
import com.example.weathercompose.ui.navigation.NavigationRoute
import com.example.weathercompose.ui.theme.CastleMoat
import com.example.weathercompose.ui.theme.Coal
import com.example.weathercompose.ui.theme.CoalBlack
import com.example.weathercompose.ui.theme.Fashionista20PerDarker
import com.example.weathercompose.ui.theme.HiloBay
import com.example.weathercompose.ui.theme.LimoScene40PerDarker
import com.example.weathercompose.ui.theme.RomanSilver
import com.example.weathercompose.ui.theme.VeryDarkShadeCyanBlue
import com.example.weathercompose.ui.viewmodel.ForecastViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainScreenCompose"

private const val COLOR_TRANSITION_ANIMATION_DURATION: Int = 700

@Composable
fun MainScreen(widgetLocationId: Long) {
    val forecastViewModel: ForecastViewModel = koinViewModel()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()

    val settingsTemperatureUnit by forecastViewModel.settingsTemperatureUnit.collectAsState()

    var weatherAndDayTimeState by rememberSaveable {
        mutableStateOf(WeatherAndDayTimeState.NO_PRECIPITATION_DAY)
    }
    var locationName by rememberSaveable { mutableStateOf("") }
    var isLocationNameVisible by rememberSaveable { mutableStateOf(true) }
    var isTemperatureUnitDialogVisible by rememberSaveable { mutableStateOf(false) }
    var isForecastUpdateFrequencyDialogVisible by rememberSaveable { mutableStateOf(false) }
    var isNoInternetDialogVisible by rememberSaveable { mutableStateOf(false) }
    val widgetsBackgroundColor by animateColorAsState(
        targetValue = weatherAndDayTimeState.toSecondaryBackgroundColor(),
        animationSpec = tween(durationMillis = 700),
        label = "Animated secondary background color",
    )

    val onLocationNameSet = { name: String -> locationName = name }
    val onLocationNameVisibilityChange = { isVisible: Boolean -> isLocationNameVisible = isVisible }

    val onAppearanceStateChange = { state: WeatherAndDayTimeState ->
        weatherAndDayTimeState = state
    }
    val onTemperatureUnitDialogOptionChoose = { isTemperatureUnitDialogVisible = true }
    val onForecastUpdateFrequencyOptionClick = { isForecastUpdateFrequencyDialogVisible = true }

    if (isTemperatureUnitDialogVisible) {
        TemperatureDialog(
            temperatureUnit = settingsTemperatureUnit,
            onDismiss = { isTemperatureUnitDialogVisible = false },
            onConfirm = { selectedOption ->
                isTemperatureUnitDialogVisible = false
                coroutineScope.launch {
                    val unit = TemperatureUnit.entries[selectedOption]
                    forecastViewModel.setCurrentTemperatureUnit(unit)
                }
            },
            widgetsBackgroundColor = widgetsBackgroundColor,
        )
    }

    if (isForecastUpdateFrequencyDialogVisible) {
        ForecastUpdateFrequencyDialog(
            updateFrequency = forecastViewModel.forecastUpdateFrequencyInHours,
            onDismiss = { isForecastUpdateFrequencyDialogVisible = false },
            onConfirm = { selectedOption ->
                isForecastUpdateFrequencyDialogVisible = false
                coroutineScope.launch {
                    forecastViewModel.setForecastUpdateFrequency(selectedOption)
                }
            },
            widgetsBackgroundColor = widgetsBackgroundColor,
        )
    }

    if (isNoInternetDialogVisible) {
        NoInternetDialog(
            onDismiss = { isNoInternetDialogVisible = false },
            onConfirm = { context.startActivity(Intent(Settings.ACTION_SETTINGS)) },
            widgetsBackgroundColor = widgetsBackgroundColor,
        )
    }

    LaunchedEffect(widgetLocationId) {
        if (widgetLocationId != 0L) {
            navController.navigate(NavigationRoute.Forecast) {
                popUpTo(0) { inclusive = true }
            }

            val forecastBackStackEntry = navController.getBackStackEntry(NavigationRoute.Forecast)
            forecastBackStackEntry.savedStateHandle[LOCATION_ID] = widgetLocationId
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        if (!forecastViewModel.isNetworkAvailable()) {
            isNoInternetDialogVisible = true
        }
    }

    MainScreenContent(
        navController = navController,
        forecastViewModel = forecastViewModel,
        widgetsBackgroundColor = widgetsBackgroundColor,
        weatherAndDayTimeState = weatherAndDayTimeState,
        onPrecipitationConditionChange = onAppearanceStateChange,
        onTemperatureUnitOptionClick = onTemperatureUnitDialogOptionChoose,
        onForecastUpdateFrequencyOptionClick = onForecastUpdateFrequencyOptionClick,
        locationName = locationName,
        onLocationNameSet = onLocationNameSet,
        isLocationNameVisible = isLocationNameVisible,
        onLocationNameVisibilityChange = onLocationNameVisibilityChange,
    )
}

@Composable
private fun MainScreenContent(
    navController: NavHostController,
    forecastViewModel: ForecastViewModel,
    widgetsBackgroundColor: Color,
    weatherAndDayTimeState: WeatherAndDayTimeState,
    onPrecipitationConditionChange: (WeatherAndDayTimeState) -> Unit,
    onTemperatureUnitOptionClick: () -> Unit,
    onForecastUpdateFrequencyOptionClick: () -> Unit,
    locationName: String,
    onLocationNameSet: (String) -> Unit,
    isLocationNameVisible: Boolean,
    onLocationNameVisibilityChange: (Boolean) -> Unit,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = currentBackStackEntry?.destination

    val onNavigateLocationsManagerScreen = {
        navController.navigate(NavigationRoute.LocationsManager)
    }

    AnimatedStatefulGradientBackground(
        weatherAndDayTimeState = weatherAndDayTimeState,
        modifier = Modifier.fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                ForecastScreenTopAppBar(
                    destination = destination,
                    locationName = locationName,
                    isLocationNameVisible = isLocationNameVisible,
                    onTemperatureUnitOptionClick = onTemperatureUnitOptionClick,
                    onForecastUpdateFrequencyOptionClick = onForecastUpdateFrequencyOptionClick,
                    onNavigateToLocationsManagerScreen = onNavigateLocationsManagerScreen,
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
                    forecastViewModel = forecastViewModel,
                    weatherAndDayTimeState = weatherAndDayTimeState,
                    onWeatherAndDayTimeStateChange = onPrecipitationConditionChange,
                    onLocationNameSet = onLocationNameSet,
                    onLocationNameVisibilityChange = onLocationNameVisibilityChange,
                    widgetsBackgroundColor = widgetsBackgroundColor,
                )
            }
        }
    }
}

@Composable
fun AnimatedStatefulGradientBackground(
    weatherAndDayTimeState: WeatherAndDayTimeState,
    modifier: Modifier = Modifier,
    durationMillis: Int = COLOR_TRANSITION_ANIMATION_DURATION,
    content: @Composable () -> Unit
) {
    val targetColors = getGradientByWeatherAndDayTimeState(weatherAndDayTimeState)

    val topColor by animateColorAsState(
        targetValue = targetColors[0],
        animationSpec = tween(durationMillis = durationMillis),
        label = "gradientColorTop"
    )

    val bottomColor by animateColorAsState(
        targetValue = targetColors[1],
        animationSpec = tween(durationMillis = durationMillis),
        label = "gradientColorBottom"
    )

    val colorStops = arrayOf(
        0.0f to topColor,
        1.0f to bottomColor
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

private fun getGradientByWeatherAndDayTimeState(
    weatherAndDayTimeState: WeatherAndDayTimeState,
): List<Color> {
    return when (weatherAndDayTimeState) {
        WeatherAndDayTimeState.NO_PRECIPITATION_DAY -> listOf(
            CastleMoat, Coal
        )

        WeatherAndDayTimeState.NO_PRECIPITATION_NIGHT -> listOf(
            VeryDarkShadeCyanBlue, CoalBlack
        )

        WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_DAY -> listOf(
            RomanSilver, HiloBay
        )

        WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_NIGHT -> listOf(
            LimoScene40PerDarker, Fashionista20PerDarker
        )
    }
}