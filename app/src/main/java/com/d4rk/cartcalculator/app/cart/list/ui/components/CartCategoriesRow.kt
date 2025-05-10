package com.d4rk.cartcalculator.app.cart.list.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallHorizontalSpacer
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.cart.list.domain.model.ui.CartCategory
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

@Composable
fun CartCategoriesRow(cart : ShoppingCartTable) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CartCategoryItem(cartCategory = CartCategory(icon = Icons.Outlined.Smartphone , text = stringResource(id = R.string.cart_category_device)))
        SmallHorizontalSpacer()
        if (cart.sharedCart) {
            CartCategoryItem(cartCategory = CartCategory(icon = Icons.Outlined.Share , text = stringResource(R.string.shared)))
        }
        else {
            CartCategoryItem(cartCategory = CartCategory(icon = Icons.Outlined.Security , text = stringResource(R.string.owner)))
        }
    }
}