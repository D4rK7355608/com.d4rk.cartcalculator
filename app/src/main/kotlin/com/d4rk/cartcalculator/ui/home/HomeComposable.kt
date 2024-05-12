package com.d4rk.cartcalculator.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.dialogs.NewCartDialog
import com.d4rk.cartcalculator.utils.bounceClick

@Composable
fun HomeComposable() {
    val openDialog = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {





        if (openDialog.value) {
            NewCartDialog(
                onDismiss = { openDialog.value = false },
                onCartCreated = { cart ->
                    openDialog.value = false
                }
            )
        }

        ExtendedFloatingActionButton(
            modifier = Modifier
                .bounceClick()
                .align(Alignment.BottomEnd),
            text = { Text(stringResource(R.string.add_new_cart)) },
            onClick = {
                openDialog.value = true
            },
            icon = {
                Icon(
                    Icons.Outlined.AddShoppingCart,
                    contentDescription = null
                )
            }
        )
    }
}