package com.d4rk.cartcalculator.core.ui.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.ButtonIconSpacer
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R

@Composable
fun NoDataScreen(
    text: Int = R.string.no_carts_available ,
    icon: ImageVector = Icons.Default.Info ,
    showRetry: Boolean = false ,
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(align = Alignment.Center)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                        .size(size = 58.dp)
                        .padding(bottom = SizeConstants.LargeSize),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(id = text) ,
                style = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center) ,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (showRetry) {
                LargeVerticalSpacer()
                Button(onClick = onRetry, modifier = Modifier.bounceClick()) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier
                                .size(size = SizeConstants.IconSize)
                    )
                    ButtonIconSpacer()
                    Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.try_again))
                }
            }
        }
    }
}