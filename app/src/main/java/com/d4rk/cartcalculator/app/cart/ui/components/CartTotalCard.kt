package com.d4rk.cartcalculator.app.cart.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ButtonIconSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ExtraLargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.NavigationBarsVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.cart.domain.model.UiCartScreen

@Composable
fun CartTotalCard(uiState : UiCartScreen) {
    Card(
        modifier = Modifier
                .fillMaxWidth()
                .height(height = 144.dp)
                .clip(shape = RoundedCornerShape(topStart = SizeConstants.LargeSize , topEnd = SizeConstants.LargeSize)) ,
        shape = RoundedCornerShape(topStart = SizeConstants.LargeSize , topEnd = SizeConstants.LargeSize) ,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.fillMaxSize() , verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally) {
            ExtraLargeVerticalSpacer()
            Row(horizontalArrangement = Arrangement.Center , verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.ShoppingCartCheckout , contentDescription = "total icon")
                ButtonIconSpacer()
                Text(modifier = Modifier.animateContentSize() , text = stringResource(id = R.string.total) , style = MaterialTheme.typography.titleLarge)
            }
            SmallVerticalSpacer()
            Row(horizontalArrangement = Arrangement.Center , verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Paid , contentDescription = "price icon")
                ButtonIconSpacer()
                PriceText(price = uiState.totalPrice.toFloat() , currency = uiState.selectedCurrency)
            }
            ExtraLargeVerticalSpacer()
            NavigationBarsVerticalSpacer()
        }
    }
}