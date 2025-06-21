package com.example.weathercompose.ui.compose.widget_configuration_screen

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import com.example.weathercompose.R
import com.example.weathercompose.ui.compose.dialog.WidgetAlarmDialog
import com.example.weathercompose.ui.model.LocationOptionItem
import com.example.weathercompose.ui.theme.CastleMoat
import com.example.weathercompose.ui.theme.Coal
import com.example.weathercompose.ui.theme.Liberty
import com.example.weathercompose.ui.viewmodel.WidgetsConfigureViewModel
import com.example.weathercompose.utils.canScheduleExactAlarms
import com.example.weathercompose.widget.ForecastWidget
import com.example.weathercompose.widget.WidgetTemperatureUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun WidgetConfigurationScreen(
    viewModel: WidgetsConfigureViewModel,
    glanceId: GlanceId,
    paddingValues: PaddingValues,
    setResultOKAndFinish: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val allowedToShowWidgetAlarmDialogState by
    viewModel.getAllowedToShowWidgetAlarmDialogState().collectAsState(null)
    val locationOptionsState by viewModel.locationOptionsState.collectAsState()
    val selectedLocationId by viewModel.selectedLocationId.collectAsState()
    val selectedTemperatureUnitOrdinal by viewModel.selectedWidgetTemperatureUnit.collectAsState()

    var isDialogVisible by rememberSaveable { mutableStateOf(false) }

    val colorStops = arrayOf(
        0.0f to CastleMoat,
        1.0f to Coal,
    )

    val gradientBackground = Brush.verticalGradient(
        colorStops = colorStops,
    )

    LaunchedEffect(locationOptionsState, glanceId) {
        viewModel.loadWidgetState(context = context, glanceId = glanceId)
    }

    LaunchedEffect(allowedToShowWidgetAlarmDialogState) {
        if (allowedToShowWidgetAlarmDialogState == true) {
            isDialogVisible = true
        }
    }

    val confirmWidgetConfigurations = {
        coroutineScope.launch(Dispatchers.IO) {
            viewModel.saveWidgetState(context = context, glanceId = glanceId)
            ForecastWidget().update(context = context, id = glanceId)
            setResultOKAndFinish()
        }
    }

    WidgetConfigurationContent(
        locationOptions = locationOptionsState,
        onLocationOptionItemClick = viewModel::selectLocation,
        selectedLocationId = selectedLocationId,
        selectedTemperatureUnit = selectedTemperatureUnitOrdinal,
        onTemperatureUnitOptionClick = viewModel::selectTemperatureUnt,
        onConfirmWidgetConfiguration = confirmWidgetConfigurations,
        paddingValues = paddingValues,
        background = gradientBackground,
    )

    val onDismiss = { isDialogVisible = false }
    val onDoNotShowAgain = {
        coroutineScope.launch {
            viewModel.setAllowedToShowWidgetAlarmDialogState(
                isAllowedToShowWidgetAlarmDialog = false
            )
            isDialogVisible = false
        }
    }
    val onConfirm = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
            isDialogVisible = false
        }
    }

    val shouldShowDialog = isDialogVisible && !canScheduleExactAlarms(context = context)
    if (shouldShowDialog) {
        WidgetAlarmDialog(
            onConfirm = { onConfirm() },
            onDoNotShowAgain = { onDoNotShowAgain() },
            onDismiss = { onDismiss() },
        )
    }
}

@Composable
private fun WidgetConfigurationContent(
    locationOptions: List<LocationOptionItem>,
    onLocationOptionItemClick: (Long) -> Unit,
    selectedLocationId: Long?,
    selectedTemperatureUnit: WidgetTemperatureUnit,
    onTemperatureUnitOptionClick: (WidgetTemperatureUnit) -> Unit,
    onConfirmWidgetConfiguration: () -> Job,
    paddingValues: PaddingValues,
    background: Brush,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = background)
            .padding(paddingValues = paddingValues),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Temperature unit:",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp, start = 15.dp),
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
            )
            WidgetConfigurationTemperatureUnitOptionsList(
                selectedTemperatureUnit = selectedTemperatureUnit,
                onTemperatureUnitOptionClick = onTemperatureUnitOptionClick
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Observed location:",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp, start = 15.dp),
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
            )
            WidgetConfigurationLocationOptionList(
                selectedLocationId = selectedLocationId,
                locations = locationOptions,
                onLocationOptionItemClick = onLocationOptionItemClick,
            )
        }

        ConfirmLocationButton(
            onButtonClick = onConfirmWidgetConfiguration,
            backgroundColor = Liberty
        )
    }
}

@Composable
private fun ConfirmLocationButton(
    onButtonClick: () -> Job,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
) {
    Button(
        onClick = { onButtonClick() },
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(bottom = 20.dp),
        colors = ButtonColors(
            containerColor = backgroundColor,
            contentColor = Color.White,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.Gray,
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = stringResource(R.string.confirm_button_text),
            modifier = Modifier
                .padding(
                    vertical = 3.5.dp,
                ),
            color = Color.White,
            fontSize = 16.sp,
        )
    }
}