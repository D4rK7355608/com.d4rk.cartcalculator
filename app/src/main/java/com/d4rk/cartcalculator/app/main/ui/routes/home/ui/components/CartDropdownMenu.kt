package com.d4rk.cartcalculator.app.main.ui.routes.home.ui.components

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
import androidx.compose.ui.res.stringResource
import com.d4rk.cartcalculator.R

@Composable
fun CartDropdownMenu(expanded : Boolean , onDismissRequest : () -> Unit , onDelete : () -> Unit , onShare : () -> Unit , onOpen : () -> Unit) {
    Box {
        IconButton(onClick = { onOpen() }) {
            Icon(imageVector = Icons.Outlined.MoreVert , contentDescription = null)
        }
        DropdownMenu(expanded = expanded , onDismissRequest = { onDismissRequest() }) {
            DropdownMenuItem(text = { Text(text = stringResource(id = R.string.delete_cart)) } , onClick = { onDelete() } , leadingIcon = { Icon(Icons.Outlined.Delete , contentDescription = null) })
            DropdownMenuItem(text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.share)) } , onClick = { onShare() } , leadingIcon = { Icon(Icons.Outlined.Share , contentDescription = null) })
        }
    }
}