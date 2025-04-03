package com.example.weathercompose.ui.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.weathercompose.ui.compose.MainScreen
import com.example.weathercompose.ui.theme.WeatherComposeTheme

class MainActivity : ComponentActivity() {
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
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}