package com.d4rk.cartcalculator.app.cart.list.ui.components

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.dropdown.CommonDropdownMenuItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.cartcalculator.R

@Composable
fun CartDropdownMenu(
    expanded : Boolean , onDismissRequest : () -> Unit , onDelete : () -> Unit , onShare : () -> Unit , onOpen : () -> Unit , onRename : () -> Unit
) {
    val view : View = LocalView.current
    val hapticFeedback : HapticFeedback = LocalHapticFeedback.current

    Box {
        IconButton(modifier = Modifier.bounceClick() , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            hapticFeedback.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.ContextClick)
            onOpen()
        }) {
            Icon(imageVector = Icons.Outlined.MoreVert , contentDescription = stringResource(id = R.string.more_options))
        }
        DropdownMenu(expanded = expanded , onDismissRequest = { onDismissRequest() }) {
            CommonDropdownMenuItem(
                textResId = R.string.rename_cart,
                icon = Icons.Outlined.Edit,
                onClick = onRename
            )
            CommonDropdownMenuItem(
                textResId = com.d4rk.android.libs.apptoolkit.R.string.share,
                icon = Icons.Outlined.Share,
                onClick = onShare
            )
            CommonDropdownMenuItem(
                textResId = R.string.delete_cart,
                icon = Icons.Outlined.Delete,
                onClick = onDelete
            )
        }
    }
}
