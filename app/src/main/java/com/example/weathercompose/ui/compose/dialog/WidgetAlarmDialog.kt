package com.example.weathercompose.ui.compose.dialog

import android.content.Context
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.weathercompose.R
import com.example.weathercompose.ui.theme.Liberty

@Composable
fun WidgetAlarmDialog(
    onConfirm: () -> Unit,
    onDoNotShowAgain: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = true,
        )
    ) {
        Card(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                WidgetAlarmDialogTitle()
                Spacer(Modifier.height(20.dp))
                WidgetAlarmDialogTextContent()
                Spacer(Modifier.height(20.dp))
                WidgetAlarmDialogButtons(
                    onConfirm = onConfirm,
                    onDoNotShowAgain = onDoNotShowAgain,
                    onDismiss = onDismiss,
                )
            }
        }
    }
}

@Composable
private fun WidgetAlarmDialogTitle() {
    Text(
        text = stringResource(R.string.widget_alarm_dialog_title),
        modifier = Modifier.fillMaxWidth(),
        color = Color.Black,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Justify,
    )
}

@Composable
private fun WidgetAlarmDialogTextContent() {
    val paragraphStyle = ParagraphStyle(
        textIndent = TextIndent(firstLine = 16.sp)
    )

    val paragraphOne = stringResource(
        id = R.string.widget_alarm_dialog_permission_request_paragraph_one
    )
    val paragraphTwo = stringResource(
        id = R.string.widget_alarm_dialog_permission_request_paragraph_two
    )
    val paragraphThree = stringResource(
        id = R.string.widget_alarm_dialog_permission_request_paragraph_three
    )

    val text = buildAnnotatedString {
        withStyle(paragraphStyle) {
            append(paragraphOne + "\n")
            append(paragraphTwo + "\n")
            append(paragraphThree)
        }
    }

    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Black,
        fontSize = 14.sp,
        textAlign = TextAlign.Justify,
    )
}

@Composable
private fun WidgetAlarmDialogButtons(
    onConfirm: () -> Unit,
    onDoNotShowAgain: () -> Unit,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = { onConfirm() },
        ) {
            Text(
                text = stringResource(R.string.go_to_settings_text_button),
                color = Liberty,
                fontSize = 14.sp,
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = { onDoNotShowAgain() },
        ) {
            Text(
                text = stringResource(R.string.do_not_show_again_text_button),
                color = Liberty,
                fontSize = 14.sp
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = { onDismiss() },
        ) {
            Text(
                text = stringResource(android.R.string.cancel).uppercase(),
                color = Liberty,
                fontSize = 14.sp
            )
        }
    }
}