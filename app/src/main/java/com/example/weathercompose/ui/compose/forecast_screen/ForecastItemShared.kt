package com.example.weathercompose.ui.compose.forecast_screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.LocalContentColor

@Composable
fun IconWithLabelHorizontal(
    @DrawableRes iconRes: Int,
    labelText: String,
    modifier: Modifier = Modifier,
    iconTint: Color = LocalContentColor.current,
) {
    ConstraintLayout(modifier = modifier.padding(vertical = 1.5.dp)) {
        val (icon, text) = createRefs()

        val iconModifier = Modifier.constrainAs(icon) {
            top.linkTo(parent.top, margin = 10.dp)
            bottom.linkTo(parent.bottom, margin = 10.dp)
            start.linkTo(parent.start, margin = 10.dp)
        }

        val textModifier = Modifier.constrainAs(text) {
            top.linkTo(icon.top)
            bottom.linkTo(icon.bottom)
            start.linkTo(icon.end, margin = 8.dp)
        }

        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "Clock icon",
            modifier = iconModifier.size(20.dp),
            tint = iconTint,
        )

        Text(
            text = labelText,
            modifier = textModifier,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}