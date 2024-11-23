package com.d4rk.cartcalculator.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.d4rk.cartcalculator.ui.components.animations.bounceClick

@Composable
fun AnimatedExtendedFloatingActionButton(
    visible: Boolean = true,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: (@Composable () -> Unit)? = null,
) {
    var isInitiallyVisible by rememberSaveable { mutableStateOf(false) }

    SideEffect {
        if (!isInitiallyVisible) {
            isInitiallyVisible = true
        }
    }

    val animationSpec = tween<Float>(
        durationMillis = 300, delayMillis = 0, easing = FastOutSlowInEasing
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f, animationSpec = animationSpec, label = "Alpha"
    )
    val offsetX by animateFloatAsState(
        targetValue = if (visible) 0f else 50f, animationSpec = animationSpec, label = "OffsetX"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 50f, animationSpec = animationSpec, label = "OffsetY"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f, animationSpec = animationSpec, label = "Scale"
    )

    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = icon,
        text = text ?: {},
        modifier = Modifier
                .bounceClick()
                .graphicsLayer {
                    this.alpha = alpha
                    this.translationX = offsetX
                    this.translationY = offsetY
                    this.scaleX = scale
                    this.scaleY = scale
                }
    )
}