package com.d4rk.cartcalculator.core.ui.snackbar

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.utils.constants.ui.SizeConstants

@Composable
fun StatusSnackbarHost(snackBarHostState: SnackbarHostState , navController: NavController , view: View) {
    androidx.compose.material3.SnackbarHost(snackBarHostState) { snackBarData ->
        val isError : Boolean = navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>("currentSnackbarIsError") ?: false
        val snackbarContentColor : Color = if (isError) MaterialTheme.colorScheme.error else SnackbarDefaults.contentColor

        Snackbar(modifier = Modifier.padding(all = SizeConstants.LargeSize) , containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.inverseSurface , contentColor = snackbarContentColor , action = {
            IconButton(modifier = Modifier.bounceClick() , onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                snackBarData.dismiss()
            }) {
                Icon(imageVector = Icons.Outlined.Close , contentDescription = "Close Snackbar" , tint = snackbarContentColor)
            }
        }) {
            Text(text = snackBarData.visuals.message)
        }
    }
}