package com.d4rk.cartcalculator.app.cart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.ButtonIconSpacer
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.cart.domain.model.UiCartScreen
import java.util.Locale


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
        Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.Center , verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.ShoppingCartCheckout , contentDescription = "total icon")
                    ButtonIconSpacer()
                    Text(text = stringResource(id = R.string.total) , style = MaterialTheme.typography.titleLarge)
                }
                Spacer(modifier = Modifier.height(height = SizeConstants.SmallSize))
                Row(horizontalArrangement = Arrangement.Center , verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Paid , contentDescription = "price icon")
                    ButtonIconSpacer()
                    Text(text = String.format(Locale.getDefault() , "%.1f" , uiState.totalPrice.toFloat()).removeSuffix(".0") , style = MaterialTheme.typography.headlineSmall , fontWeight = FontWeight.Bold)
                    ButtonIconSpacer()
                    Text(text = uiState.selectedCurrency , style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    }
}