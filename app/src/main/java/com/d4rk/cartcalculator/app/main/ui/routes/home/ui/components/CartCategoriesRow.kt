package com.d4rk.cartcalculator.app.main.ui.routes.home.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.SmallHorizontalSpacer
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.model.CartCategory

@Composable
fun CartCategoriesRow() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CartCategoryItem(
            cartCategory = CartCategory(
                icon = Icons.Outlined.Smartphone , text = "On this device"
            )
        )
        SmallHorizontalSpacer()
        CartCategoryItem(
            cartCategory = CartCategory(
                icon = Icons.Outlined.Security , text = "Owner"
            )
        )
    }
}