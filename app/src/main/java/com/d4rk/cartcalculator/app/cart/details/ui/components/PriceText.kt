package com.d4rk.cartcalculator.app.cart.details.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.util.Locale

@Composable
fun PriceText(price : Float , currency : String , modifier : Modifier = Modifier) {
    val formattedPrice : String = String.format(Locale.getDefault() , "%.1f" , price).removeSuffix(suffix = ".0") + " $currency"

    Row(modifier = modifier) {
        formattedPrice.forEach { char : Char ->
            if (char.isDigit()) {
                AnimatedDigit(digit = char)
            }
            else {
                Text(text = char.toString() , style = MaterialTheme.typography.headlineSmall , fontWeight = MaterialTheme.typography.headlineSmall.fontWeight)
            }
        }
    }
}

// TODO: lib
@Composable
fun AnimatedDigit(digit : Char) {
    AnimatedContent(
        targetState = digit , transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically(animationSpec = tween(durationMillis = 400) , initialOffsetY = { fullHeight : Int -> - fullHeight }) + fadeIn(animationSpec = tween(durationMillis = 400))).togetherWith(
                    exit = slideOutVertically(
                        animationSpec = tween(durationMillis = 400) , targetOffsetY = { fullHeight : Int -> fullHeight }) + fadeOut(animationSpec = tween(durationMillis = 400))
                )
            }
            else {
                (slideInVertically(animationSpec = tween(durationMillis = 400) , initialOffsetY = { fullHeight : Int -> fullHeight }) + fadeIn(animationSpec = tween(durationMillis = 400))).togetherWith(
                    exit = slideOutVertically(
                        animationSpec = tween(durationMillis = 400) , targetOffsetY = { fullHeight : Int -> - fullHeight }) + fadeOut(animationSpec = tween(durationMillis = 400))
                )
            }.using(sizeTransform = SizeTransform(clip = false))
        }) { targetDigit : Char ->
        Text(text = targetDigit.toString() , style = MaterialTheme.typography.headlineSmall , fontWeight = MaterialTheme.typography.headlineSmall.fontWeight)
    }
}