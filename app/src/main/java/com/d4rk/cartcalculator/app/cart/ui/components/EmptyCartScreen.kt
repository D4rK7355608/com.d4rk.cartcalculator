package com.d4rk.cartcalculator.app.cart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R

@Composable
fun EmptyCartScreen(modifier : Modifier = Modifier) {
    Column(modifier = modifier , horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.SpaceBetween) {
        LazyColumn(modifier = Modifier.weight(1f) , horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.Center) {
            item {
                NoDataScreen(text = R.string.your_shopping_cart_is_empty , icon = Icons.Outlined.ShoppingCart)
            }
        }
        InfoMessageSection(
            message = "It appears your shopping cart is currently empty. To begin adding items, you can easily navigate to your desired product categories and select them. Once you've found something you'd like, simply tap on the shopping cart icon, conveniently located in the upper left corner, to add it to your cart." ,
            modifier = Modifier.padding(all = SizeConstants.LargeSize)
        )
    }
}