package com.d4rk.cartcalculator.app.home.ui.components

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
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
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.cartcalculator.R

@Composable
fun CartDropdownMenu(
    expanded : Boolean , onDismissRequest : () -> Unit , onDelete : () -> Unit , onShare : () -> Unit , onOpen : () -> Unit , onRename : () -> Unit
) {
    val view : View = LocalView.current
    Box {
        IconButton(modifier = Modifier.bounceClick() , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            onOpen()
        }) {
            Icon(imageVector = Icons.Outlined.MoreVert , contentDescription = stringResource(id = R.string.more_options))
        }
        DropdownMenu(expanded = expanded , onDismissRequest = { onDismissRequest() }) {
            DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(text = stringResource(id = R.string.rename_cart)) } , onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onRename()
            } , leadingIcon = { Icon(Icons.Outlined.Edit , contentDescription = stringResource(id = R.string.rename_cart)) })
            DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.share)) } , onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onShare()
            } , leadingIcon = { Icon(Icons.Outlined.Share , contentDescription = stringResource(id = R.string.share_cart)) })
            DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(text = stringResource(id = R.string.delete_cart)) } , onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onDelete()
            } , leadingIcon = { Icon(Icons.Outlined.Delete , contentDescription = stringResource(id = R.string.delete_cart)) })
        }
    }
}