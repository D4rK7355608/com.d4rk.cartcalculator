package com.d4rk.cartcalculator.app.onboarding.ui.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.MediumHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.settings.cart.list.ui.dialogs.SelectCurrencyAlertDialog
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun CartBehaviorOnboardingTab() {
    val dataStore: DataStore = koinInject()
    val openCartsAfterCreation by dataStore.openCartsAfterCreation.collectAsState(initial = true)
    val preferredCurrency by dataStore.getCurrency().collectAsState(initial = "")
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    var showCurrencyDialog: Boolean by remember { mutableStateOf(value = false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = SizeConstants.ExtraLargeIncreasedSize),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = stringResource(id = R.string.onboarding_cart_settings_icon_description),
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(id = R.string.onboarding_cart_settings_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = stringResource(id = R.string.onboarding_cart_settings_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LargeVerticalSpacer()

        PreferenceCard(
            title = stringResource(id = R.string.onboarding_open_carts_after_creation),
            summary = stringResource(id = R.string.onboarding_summary_open_carts_after_creation),
            control = {
                Switch(
                    checked = openCartsAfterCreation,
                    onCheckedChange = { isChecked ->
                        coroutineScope.launch {
                            dataStore.saveOpenCartsAfterCreation(isEnabled = isChecked)
                        }
                    }
                )
            }
        )

        PreferenceCard(
            title = stringResource(id = R.string.select_currency),
            summary = preferredCurrency.ifEmpty { stringResource(R.string.onboarding_summary_select_currency_placeholder) },
            onClick = { showCurrencyDialog = true },
            control = {
                Icon(
                    imageVector = Icons.Outlined.AttachMoney,
                    contentDescription = stringResource(id = R.string.select_currency_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }

    if (showCurrencyDialog) {
        SelectCurrencyAlertDialog(
            dataStore = dataStore,
            onDismiss = { showCurrencyDialog = false },
            onCurrencySelected = { selectedCurrency ->
                coroutineScope.launch {
                    dataStore.saveCurrency(selectedCurrency)
                }
                showCurrencyDialog = false
            }
        )
    }
}

@Composable
fun PreferenceCard(
    title: String,
    summary: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    control: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = SizeConstants.ExtraTinySize),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SizeConstants.LargeSize),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            if (control != null) {
                MediumHorizontalSpacer()
                control()
            }
        }
    }
}