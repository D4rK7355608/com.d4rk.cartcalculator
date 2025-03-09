package com.d4rk.cartcalculator.app.main.ui.routes.home.ui.components

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.cartcalculator.R

@Composable
fun CartDropdownMenu(expanded : Boolean , onDismissRequest : () -> Unit , onDelete : () -> Unit , onShare : () -> Unit , onOpen : () -> Unit) {
    val view : View = LocalView.current

    Box {
        IconButton(modifier = Modifier.bounceClick() , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            onOpen()
        }) {
            Icon(imageVector = Icons.Outlined.MoreVert , contentDescription = null)
        }
        DropdownMenu(expanded = expanded , onDismissRequest = { onDismissRequest() }) {
            DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(text = stringResource(id = R.string.delete_cart)) } , onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onDelete()
            } , leadingIcon = { Icon(Icons.Outlined.Delete , contentDescription = null) })
            DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.share)) } , onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onShare()
            } , leadingIcon = { Icon(Icons.Outlined.Share , contentDescription = null) })
        }
    }
}