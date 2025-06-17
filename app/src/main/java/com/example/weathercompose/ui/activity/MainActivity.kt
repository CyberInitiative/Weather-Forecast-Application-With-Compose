package com.example.weathercompose.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import com.example.weathercompose.ui.compose.main_screen.MainScreen
import com.example.weathercompose.ui.theme.WeatherComposeTheme
import com.example.weathercompose.widget.LOCATION_ID_PARAM

class MainActivity : ComponentActivity() {
    private var widgetLocationId by mutableLongStateOf(0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        widgetLocationId = intent?.getLongExtra(LOCATION_ID_PARAM, 0L) ?: 0L

        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            WeatherComposeTheme {
                MainScreen(widgetLocationId = widgetLocationId)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        widgetLocationId = 0L
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        widgetLocationId = intent.getLongExtra(LOCATION_ID_PARAM, 0L)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}