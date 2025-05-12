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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.weathercompose.R
import com.example.weathercompose.data.model.ForecastUpdateFrequency
import com.example.weathercompose.ui.theme.IntercoastalGray
import com.example.weathercompose.ui.theme.Liberty

@Composable
fun ForecastUpdateFrequencyDialog(
    updateFrequency: ForecastUpdateFrequency,
    onDismiss: () -> Unit,
    onConfirm: (ForecastUpdateFrequency) -> Unit
) {
    val frequencies = stringArrayResource(R.array.update_frequency_options)

    val options = arrayOf(
        ForecastUpdateFrequency.ONE_HOUR to frequencies[0],
        ForecastUpdateFrequency.TWO_HOURS to frequencies[1],
        ForecastUpdateFrequency.THREE_HOURS to frequencies[2],
        ForecastUpdateFrequency.SIX_HOURS to frequencies[3],
        ForecastUpdateFrequency.TWELVE_HOURS to frequencies[4],
        ForecastUpdateFrequency.TWENTY_FOUR_HOURS to frequencies[5]
    )

    var selectedOptionIndex by remember {
        mutableIntStateOf(options.indexOfFirst { it.first == updateFrequency })
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
                Text(
                    text = stringResource(R.string.forecast_update_frequency_dialog_title),
                    color = Color.Black,
                    fontSize = 18.sp,
                )
                Spacer(Modifier.height(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEachIndexed { index, (_, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                            )
                            RadioButton(
                                selected = selectedOptionIndex == index,
                                onClick = { selectedOptionIndex = index },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Liberty,
                                )
                            )
                        }

                        if (index != options.lastIndex) {
                            HorizontalDivider(
                                color = IntercoastalGray,
                                thickness = 1.2.dp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            val selectedHoursValue = options[selectedOptionIndex].first
                            onConfirm(selectedHoursValue)
                        },
                    ) {
                        Text(text = "OK", color = Liberty, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}