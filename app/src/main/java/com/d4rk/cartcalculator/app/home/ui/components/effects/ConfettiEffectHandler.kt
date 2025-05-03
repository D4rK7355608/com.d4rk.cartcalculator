package com.d4rk.cartcalculator.app.home.ui.components.effects

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun ConfettiEffectHandler(cartSize : Int) {
    val showConfetti : MutableState<Boolean> = remember { mutableStateOf<Boolean>(value = false) }
    val previousCartSize : MutableIntState = remember { mutableIntStateOf(value = 0) }

    LaunchedEffect(key1 = cartSize) {
        if (previousCartSize.intValue == 0 && cartSize == 1) {
            showConfetti.value = true
            delay(timeMillis = 3000)
            showConfetti.value = false
        }
        previousCartSize.intValue = cartSize
    }

    if (showConfetti.value) {
        KonfettiView(
            modifier = Modifier.fillMaxSize() ,
            parties = listOf(Party(speed = 0f , maxSpeed = 30f , damping = 0.9f , spread = 360 , colors = listOf(0xfce18a , 0xff726d , 0xf4306d , 0xb48def) , emitter = Emitter(duration = 100 , timeUnit = TimeUnit.MILLISECONDS).max(100) , position = Position.Relative(0.5 , 0.3)))
        )
    }
}