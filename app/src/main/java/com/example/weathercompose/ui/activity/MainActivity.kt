package com.example.weathercompose.ui.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.weathercompose.ui.compose.main_screen.MainScreen
import com.example.weathercompose.ui.theme.WeatherComposeTheme

class MainActivity : ComponentActivity() {
    //private val forecastViewModel: ForecastViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            WeatherComposeTheme {
                MainScreen()
            }
        }

        //setForecastUpdatingWorker()
    }

    /*
    private fun setForecastUpdatingWorker() {
        val workRequest =
            PeriodicWorkRequestBuilder<ForecastUpdatingWorker>(1, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

        WorkManager.getInstance(context = this).enqueueUniquePeriodicWork(
            FORECAST_UPDATING_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
     */

    companion object {
        private const val TAG = "MainActivity"
        // private const val FORECAST_UPDATING_WORK = "forecast_updating_work"
    }
}