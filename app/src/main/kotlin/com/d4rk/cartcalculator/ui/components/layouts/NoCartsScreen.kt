package com.d4rk.cartcalculator.ui.components.layouts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.ui.components.ads.AdBanner
import com.google.android.gms.ads.AdSize

@Composable
fun NoCartsScreen() {
    Text(
        text = stringResource(id = R.string.no_carts_available)
    )
    LargeVerticalSpacer()
    AdBanner(
        modifier = Modifier
                .fillMaxWidth()
                .height(AdSize.MEDIUM_RECTANGLE.height.dp) , adSize = AdSize.MEDIUM_RECTANGLE
    )
}