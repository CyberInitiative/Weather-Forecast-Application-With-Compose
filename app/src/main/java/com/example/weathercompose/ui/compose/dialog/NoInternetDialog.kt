package com.example.weathercompose.ui.compose.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.weathercompose.R
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
import com.example.weathercompose.ui.theme.HiloBay25PerDarker
import com.example.weathercompose.ui.theme.Liberty
import com.example.weathercompose.ui.theme.MediumDarkShadeCyanBlue
import com.example.weathercompose.ui.theme.Solitaire5PerDarker

@Composable
fun NoInternetDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    weatherAndDayTimeState: WeatherAndDayTimeState,
) {
    val uiElementsColor by remember(weatherAndDayTimeState) {
        mutableStateOf(
            when (weatherAndDayTimeState) {
                WeatherAndDayTimeState.NO_PRECIPITATION_DAY -> Liberty
                WeatherAndDayTimeState.NO_PRECIPITATION_NIGHT -> MediumDarkShadeCyanBlue
                WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_DAY -> HiloBay25PerDarker
                WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_NIGHT -> Solitaire5PerDarker
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = stringResource(R.string.no_internet_dialog_text),
                        color = Color.Black,
                        fontSize = 16.sp,
                    )
                }
                Spacer(Modifier.height(30.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = { onConfirm() },
                    ) {
                        Text(
                            text = stringResource(R.string.go_to_settings_text_button),
                            color = uiElementsColor,
                            fontSize = 16.sp
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                    ) {
                        Text(
                            text = stringResource(android.R.string.ok),
                            color = uiElementsColor,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}