package com.d4rk.cartcalculator.app.settings.cart.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.MediumVerticalSpacer
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun SelectCurrencyAlertDialog(dataStore : DataStore , onDismiss : () -> Unit , onCurrencySelected : (String) -> Unit) {
    val selectedCurrency : MutableState<String> = remember { mutableStateOf(value = "") }
    val currencies : List<String> = stringArrayResource(id = R.array.currency).toList()

    BasicAlertDialog(onDismiss = onDismiss , onConfirm = { onCurrencySelected(selectedCurrency.value) } , icon = Icons.Outlined.Money , title = stringResource(id = R.string.select_currency) , content = {
        SelectCurrencyAlertDialogContent(selectedCurrency = selectedCurrency , dataStore = dataStore , currencies = currencies)
    })
}

@Composable
fun SelectCurrencyAlertDialogContent(
    selectedCurrency : MutableState<String> , dataStore : DataStore , currencies : List<String> ,
) {
    LaunchedEffect(key1 = Unit) {
        selectedCurrency.value = dataStore.getCurrency().firstOrNull() ?: ""
    }

    Column {
        Text(text = stringResource(id = R.string.dialog_currency_subtitle))
        Box(
            modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f)
        ) {
            LazyColumn {
                items(count = currencies.size) { index ->
                    Row(
                        Modifier.fillMaxWidth() , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.Start
                    ) {
                        RadioButton(
                            selected = selectedCurrency.value == currencies[index] , onClick = {
                                selectedCurrency.value = currencies[index]
                            })
                        Text(
                            modifier = Modifier.padding(start = 8.dp) , text = currencies[index] , style = MaterialTheme.typography.bodyMedium.merge()
                        )
                    }
                }
            }
        }
        MediumVerticalSpacer()
        InfoMessageSection(message = stringResource(id = R.string.dialog_info_currency))
    }

    LaunchedEffect(key1 = selectedCurrency.value) {
        dataStore.saveCurrency(currency = selectedCurrency.value)
    }
}