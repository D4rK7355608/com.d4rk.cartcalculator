package com.d4rk.cartcalculator.app.cart.details.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R

@Composable
fun EmptyCartScreen(modifier : Modifier = Modifier) {
    Column(modifier = modifier , horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.SpaceBetween) {
        LazyColumn(modifier = Modifier.weight(1f) , horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.Center) {
            item {
                NoDataScreen(textMessage = R.string.your_shopping_cart_is_empty , icon = Icons.Outlined.ShoppingCart)
            }
        }
        InfoMessageSection(message = stringResource(id = R.string.empty_cart_info_message) , modifier = Modifier.padding(all = SizeConstants.LargeSize))
    }
}