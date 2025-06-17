package com.example.weathercompose.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/*
private val ClearSkyWeatherTheme = lightColorScheme(
    primary = Liberty,
    onPrimary = Color.White,
    secondary = Liberty,
    onSecondary = Color.White,
    background = CastleMoat,
    tertiary = SiberianIce,
)

@Composable
fun WeatherStateTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = ClearSkyWeatherTheme,
        typography = Typography,
        content = content
    )
}
 */

@Composable
fun WeatherComposeTheme(
    content: @Composable () -> Unit
) {

    /*
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    */

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}