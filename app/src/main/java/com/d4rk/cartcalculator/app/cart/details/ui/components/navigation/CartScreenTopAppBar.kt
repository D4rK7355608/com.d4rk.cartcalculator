package com.d4rk.cartcalculator.app.cart.details.ui.components.navigation

import android.app.Activity
import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.AnimatedIconButtonDirection
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.cart.details.domain.actions.CartEvent
import com.d4rk.cartcalculator.app.cart.details.domain.model.ui.UiCartScreen
import com.d4rk.cartcalculator.app.cart.details.ui.CartViewModel
import com.d4rk.cartcalculator.core.utils.helpers.WalletAppsHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreenTopAppBar(screenState : UiStateScreen<UiCartScreen> , viewModel : CartViewModel , activity : Activity , context : Context , scrollBehavior : TopAppBarScrollBehavior) {
    val isGooglePayInstalled : Boolean = remember { WalletAppsHelper.isGooglePayInstalled(context = context) }
    val cartButtonsVisible : Boolean = screenState.data?.cartItems?.isNotEmpty() == true

    LargeTopAppBar(title = {
        Text(text = screenState.data?.cart?.name ?: stringResource(id = R.string.shopping_cart) , modifier = Modifier.animateContentSize() , maxLines = 1 , softWrap = false , overflow = TextOverflow.Ellipsis)
    } , navigationIcon = {
        AnimatedIconButtonDirection(
            icon = Icons.AutoMirrored.Filled.ArrowBack ,
            contentDescription = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.go_back) ,
            onClick = { activity.finish() } ,
        )
    } , actions = {
        AnimatedIconButtonDirection(icon = Icons.Outlined.AddShoppingCart , contentDescription = "Add Item" , onClick = {
            viewModel.onEvent(event = CartEvent.OpenNewCartItemDialog(isOpen = true))
        } , fromRight = true)

        AnimatedIconButtonDirection(visible = isGooglePayInstalled && cartButtonsVisible , icon = Icons.Outlined.CreditCard , contentDescription = "Open Google Pay" , onClick = {
            WalletAppsHelper.openGooglePayOrWallet(context = context)
        } , durationMillis = 400 , fromRight = true)

        AnimatedIconButtonDirection(visible = cartButtonsVisible , icon = Icons.Outlined.Share , durationMillis = 500 , contentDescription = "Share Cart" , onClick = {
            viewModel.onEvent(event = CartEvent.GenerateCartShareLink(cartId = screenState.data?.cart?.cartId ?: 0))
        } , fromRight = true)
    } , scrollBehavior = scrollBehavior)
}