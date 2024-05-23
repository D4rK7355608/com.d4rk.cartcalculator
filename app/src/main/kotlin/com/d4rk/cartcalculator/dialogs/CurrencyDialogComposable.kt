package com.d4rk.cartcalculator.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.store.DataStore
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun CurrencyDialog(
    dataStore: DataStore, onDismiss: () -> Unit, onCurrencySelected: (String) -> Unit
) {
    val selectedCurrency = remember { mutableStateOf("") }
    val currencies = stringArrayResource(R.array.currency).toList()

    AlertDialog(onDismissRequest = onDismiss,
        text = { CurrencyDialogContent(selectedCurrency, dataStore, currencies) },
        icon = {
            Icon(Icons.Outlined.Money, contentDescription = null)
        },
        confirmButton = {
            TextButton(onClick = {
                onCurrencySelected(selectedCurrency.value)
            }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        })
}

@Composable
fun CurrencyDialogContent(
    selectedCurrency: MutableState<String>, dataStore: DataStore, currencies: List<String>
) {
    LaunchedEffect(Unit) {
        selectedCurrency.value = dataStore.getCurrency().firstOrNull() ?: ""
    }

    Column {
        Text(stringResource(id = R.string.dialog_currency_subtitle))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            currencies.forEach { currency ->
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(selected = selectedCurrency.value == currency,
                        onClick = { selectedCurrency.value = currency })
                    Text(
                        text = currency, style = MaterialTheme.typography.bodyMedium.merge()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
        Spacer(modifier = Modifier.height(12.dp))
        Text(stringResource(id = R.string.dialog_info_currency))
    }

    LaunchedEffect(selectedCurrency.value) {
        dataStore.saveCurrency(selectedCurrency.value)
    }
}