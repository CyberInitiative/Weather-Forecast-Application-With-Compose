package com.example.weathercompose.ui.activity

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.example.weathercompose.ui.compose.widget_configuration_screen.WidgetConfigurationScreen
import com.example.weathercompose.ui.theme.WeatherComposeTheme
import com.example.weathercompose.ui.viewmodel.WidgetsConfigureViewModel
import com.example.weathercompose.utils.scheduleWidgetsUpdate
import org.koin.androidx.viewmodel.ext.android.viewModel

class WidgetAlarmDialogHostActivity : ComponentActivity() {
    private val viewModel: WidgetsConfigureViewModel by viewModel()
    private var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var glanceId: GlanceId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWidgetId()

        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_CANCELED, resultValue)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            WeatherComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WidgetConfigurationScreen(

                        viewModel = viewModel,
                        glanceId = glanceId,
                        paddingValues = innerPadding,
                        setResultOKAndFinish = ::setResultOKAndFinish,
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        scheduleWidgetsUpdate(context = this)
    }

    private fun setWidgetId() {
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        glanceId = GlanceAppWidgetManager(applicationContext).getGlanceIdBy(appWidgetId)
    }

    private fun setResultOKAndFinish() {
        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
}