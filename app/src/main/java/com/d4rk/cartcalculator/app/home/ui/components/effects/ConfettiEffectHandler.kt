package com.d4rk.cartcalculator.app.home.ui.components.effects

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun ConfettiEffectHandler(firstCartId : Int? , cartSize : Int) {
    val context : Context = LocalContext.current
    val dataStore : DataStore = remember { DataStore.getInstance(context = context) }
    val hasSeenConfetti : Boolean by dataStore.hasSeenConfetti().collectAsState(initial = false)
    var shouldShowConfetti : Boolean by remember { mutableStateOf<Boolean>(value = false) }

    LaunchedEffect(key1 = cartSize , key2 = firstCartId , key3 = hasSeenConfetti) {
        if (cartSize > 0 && firstCartId == 1 && ! hasSeenConfetti) {
            shouldShowConfetti = true

            delay(timeMillis = 500)
            dataStore.saveHasSeenConfetti(seen = true)
        }
    }

    if (shouldShowConfetti) {
        KonfettiView(
            modifier = Modifier.fillMaxSize() ,
            parties = listOf(
                Party(
                    speed = 0f ,
                    maxSpeed = 30f ,
                    damping = 0.9f ,
                    spread = 360 ,
                    colors = listOf<Int>(0xfce18a , 0xff726d , 0xf4306d , 0xb48def) ,
                    emitter = Emitter(duration = 100 , timeUnit = TimeUnit.MILLISECONDS).max(amount = 100) ,
                    position = Position.Relative(x = 0.5 , y = 0.3)
                )
            )
        )
    }
}