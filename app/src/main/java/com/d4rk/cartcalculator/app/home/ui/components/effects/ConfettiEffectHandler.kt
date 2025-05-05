package com.d4rk.cartcalculator.app.home.ui.components.effects

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.koin.compose.koinInject
import java.util.concurrent.TimeUnit

@Composable
fun ConfettiEffectHandler(firstCartId : Int? , cartSize : Int) {
    val dataStore : DataStore = koinInject()
    val hasSeenConfetti : Boolean by dataStore.hasSeenConfetti().collectAsState(initial = false)
    val shouldShowConfetti : Boolean = remember {
        cartSize == 1 && firstCartId == 1 && ! hasSeenConfetti
    }

    if (shouldShowConfetti) {

        LaunchedEffect(key1 = Unit) {
            dataStore.saveHasSeenConfetti(seen = true)
        }

        KonfettiView(
            modifier = Modifier.fillMaxSize() , parties = listOf(
                element = Party(
                    speed = 0f , maxSpeed = 30f , damping = 0.9f , spread = 360 , colors = listOf(0xfce18a , 0xff726d , 0xf4306d , 0xb48def) , emitter = Emitter(duration = 100 , timeUnit = TimeUnit.MILLISECONDS).max(amount = 100) , position = Position.Relative(x = 0.5 , y = 0.3)
                )
            )
        )
    }
}