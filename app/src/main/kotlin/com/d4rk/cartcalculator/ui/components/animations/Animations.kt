package com.d4rk.cartcalculator.ui.components.animations

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.d4rk.cartcalculator.data.datastore.DataStore
import com.d4rk.cartcalculator.data.model.ui.button.ButtonState

@Composable
fun Modifier.bounceClick(
    animationEnabled : Boolean = true ,
) : Modifier = composed {
    var buttonState : ButtonState by remember { mutableStateOf(ButtonState.Idle) }
    val context : Context = LocalContext.current
    val dataStore : DataStore = DataStore.getInstance(context = context)
    val bouncyButtonsEnabled : Boolean by dataStore.bouncyButtons.collectAsState(initial = true)
    val scale : Float by animateFloatAsState(
        if (buttonState == ButtonState.Pressed && animationEnabled && bouncyButtonsEnabled) 0.96f else 1f ,
        label = "Button Press Scale Animation"
    )

    if (bouncyButtonsEnabled) {
        return@composed this
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .pointerInput(buttonState) {
                    awaitPointerEventScope {
                        buttonState = if (buttonState == ButtonState.Pressed) {
                            waitForUpOrCancellation()
                            ButtonState.Idle
                        }
                        else {
                            awaitFirstDown(requireUnconsumed = false)
                            ButtonState.Pressed
                        }
                    }
                }
    }
    else {
        return@composed this
    }
}

fun Modifier.hapticDrawerSwipe(drawerState : DrawerState) : Modifier = composed {
    val haptic : HapticFeedback = LocalHapticFeedback.current
    var hasVibrated : Boolean by remember { mutableStateOf(value = false) }

    LaunchedEffect(drawerState.currentValue , drawerState.targetValue) {
        if (drawerState.isAnimationRunning && ! hasVibrated) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            hasVibrated = true
        }

        if (! drawerState.isAnimationRunning) {
            hasVibrated = false
        }
    }

    return@composed this
}

fun Modifier.hapticSwipeToDismissBox(swipeToDismissBoxState: SwipeToDismissBoxState): Modifier = composed {
    val haptic: HapticFeedback = LocalHapticFeedback.current
    var hasVibrated by remember { mutableStateOf(value = false) }

    LaunchedEffect(swipeToDismissBoxState.currentValue) {
        if (swipeToDismissBoxState.currentValue != SwipeToDismissBoxValue.Settled && !hasVibrated) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            hasVibrated = true
        } else if (swipeToDismissBoxState.currentValue == SwipeToDismissBoxValue.Settled) {
            hasVibrated = false
        }
    }

    return@composed this
}