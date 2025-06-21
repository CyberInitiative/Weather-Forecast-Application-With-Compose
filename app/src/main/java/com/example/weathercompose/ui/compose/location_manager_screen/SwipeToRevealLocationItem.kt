package com.example.weathercompose.ui.compose.location_manager_screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.weathercompose.ui.model.LocationItem
import com.example.weathercompose.ui.theme.PeonyPink
import com.example.weathercompose.ui.theme.TealZeal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeToRevealLocationItem(
    locationItem: LocationItem,
    modifier: Modifier = Modifier,
    itemHeight: Dp,
    itemBackgroundColor: Color,
    onLocationItemClick: (Long) -> Unit,
    onLocationDelete: (Long) -> Unit,
    onLocationAsHomeSet: (Long) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val offsetX = remember{ Animatable(0f) }
    var areActionsRevealed by rememberSaveable { mutableStateOf(false) }
    var actionsWidth by remember { mutableFloatStateOf(0f) }

    val onSetActionsWidth = { width: Float -> actionsWidth = width }
    val onSetActionsRevealedState = { areRevealed: Boolean -> areActionsRevealed = areRevealed }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight)
    ) {
        ActionsRow(
            itemHeight = itemHeight,
            onSetActionsWidth = onSetActionsWidth,
            onDelete = { locationId ->
                coroutineScope.launch {
                    offsetX.animateTo(0f)
                    areActionsRevealed = false
                    onLocationDelete(locationId)
                }
            },
            onSetAsHome = onLocationAsHomeSet,
            locationId = locationItem.id,
            isHomeLocation = locationItem.isHomeLocation,
        )

        LocationItemContent(
            itemHeight = itemHeight,
            offsetX = offsetX,
            actionsWidth = actionsWidth,
            coroutineScope = coroutineScope,
            locationItem = locationItem,
            areActionsRevealed = areActionsRevealed,
            onLocationItemClick = onLocationItemClick,
            onSetActionsRevealedState = onSetActionsRevealedState,
            itemBackgroundColor = itemBackgroundColor,
        )
    }
}

@Composable
private fun ActionsRow(
    itemHeight: Dp,
    onSetActionsWidth: (Float) -> Unit,
    onDelete: (Long) -> Unit,
    onSetAsHome: (Long) -> Unit,
    locationId: Long,
    isHomeLocation: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .clip(shape = RoundedCornerShape(15.dp))
            .background(color = TealZeal),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .height(itemHeight)
                .clip(shape = RoundedCornerShape(15.dp))
                .onGloballyPositioned { coordinates ->
                    onSetActionsWidth(coordinates.size.width.toFloat())
                }
                .background(color = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .background(color = TealZeal),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    modifier = Modifier.size(55.dp),
                    onClick = { onSetAsHome(locationId) },
                    content = {
                        if (isHomeLocation) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Set Location As Home Icon",
                            )
                        } else {
                            Icon(
                                Icons.Outlined.Home,
                                contentDescription = "Set Location As Home Icon",
                            )
                        }
                    },
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .background(color = PeonyPink),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    modifier = Modifier.size(55.dp),
                    onClick = { onDelete(locationId) },
                    content = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Location",
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun LocationItemContent(
    itemHeight: Dp,
    offsetX: Animatable<Float, AnimationVector1D>,
    actionsWidth: Float,
    coroutineScope: CoroutineScope,
    locationItem: LocationItem,
    areActionsRevealed: Boolean,
    onLocationItemClick: (Long) -> Unit,
    onSetActionsRevealedState: (Boolean) -> Unit,
    itemBackgroundColor: Color,
) {
    ConstraintLayout(
        modifier = Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .fillMaxWidth()
            .height(itemHeight)
            .clip(shape = RoundedCornerShape(15.dp))
            .background(color = itemBackgroundColor)
            .clickable { if (!areActionsRevealed) onLocationItemClick(locationItem.id) }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    val minOffset = -actionsWidth
                    val maxOffset = 0f
                    val newOffset = (offsetX.value + delta).coerceIn(minOffset, maxOffset)
                    coroutineScope.launch {
                        offsetX.snapTo(newOffset)
                    }
                },
                onDragStopped = {
                    coroutineScope.launch {
                        val openPos = -actionsWidth
                        val closePos = 0f
                        val threshold = actionsWidth / 3

                        if (areActionsRevealed) {
                            if (offsetX.value > openPos + threshold) {
                                offsetX.animateTo(closePos)
                                onSetActionsRevealedState(false)
                            } else {
                                offsetX.animateTo(openPos)
                            }
                        } else {
                            if (offsetX.value < closePos - threshold) {
                                offsetX.animateTo(openPos)
                                onSetActionsRevealedState(true)
                            } else {
                                offsetX.animateTo(closePos)
                            }
                        }
                    }
                }
            )
    ) {
        val (weatherIcon, weatherDescriptionLabel, temperatureLabel, locationNameLabel)
                = createRefs()

        val weatherIconModifier = Modifier.constrainAs(weatherIcon) {
            top.linkTo(parent.top, margin = 10.dp)
            bottom.linkTo(parent.bottom, margin = 12.dp)
            end.linkTo(parent.end, margin = 10.dp)
        }

        val temperatureModifier = Modifier.constrainAs(temperatureLabel) {
            end.linkTo(weatherIcon.start, margin = 20.dp)
            top.linkTo(parent.top, margin = 10.dp)
        }

        val weatherDescriptionModifier = Modifier.constrainAs(weatherDescriptionLabel) {
            end.linkTo(weatherIcon.start, margin = 20.dp)
            top.linkTo(temperatureLabel.bottom, margin = 5.dp)
            bottom.linkTo(parent.bottom, margin = 10.dp)
        }

        val locationNameLabelModifier = Modifier.constrainAs(locationNameLabel) {
            start.linkTo(parent.start, margin = 12.dp)
            end.linkTo(weatherDescriptionLabel.start, margin = 30.dp)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
        }

        Icon(
            painter = painterResource(id = locationItem.currentHourWeatherIconRes),
            contentDescription = "Current hour weather icon",
            modifier = weatherIconModifier.size(45.dp),
            tint = Color.White,
        )

        Text(
            text = locationItem.currentHourTemperature,
            modifier = temperatureModifier,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
        )

        Text(
            text = stringResource(locationItem.currentHourWeatherDescription),
            modifier = weatherDescriptionModifier
                .width(100.dp),
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        Text(
            text = locationItem.name,
            modifier = locationNameLabelModifier.fillMaxWidth(),
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}