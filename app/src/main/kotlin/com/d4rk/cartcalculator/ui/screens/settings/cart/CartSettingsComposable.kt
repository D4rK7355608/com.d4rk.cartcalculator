package com.d4rk.cartcalculator.ui.screens.settings.cart

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.ui.components.preferences.PreferenceCategoryItem
import com.d4rk.android.libs.apptoolkit.ui.components.preferences.PreferenceItem
import com.d4rk.android.libs.apptoolkit.ui.components.preferences.SwitchPreferenceItem
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.ui.components.dialogs.SelectCurrencyAlertDialog
import com.d4rk.cartcalculator.ui.components.navigation.TopAppBarScaffoldWithBackButton
import kotlinx.coroutines.launch

@Composable
fun CartSettingsComposable(activity : CartSettingsActivity) {
    val dataStore = AppCoreManager.dataStore
    val showDialog = remember { mutableStateOf(value = false) }
    val openCartsAfterCreation by dataStore.openCartsAfterCreation.collectAsState(initial = true)
    val coroutineScope = rememberCoroutineScope()

    TopAppBarScaffoldWithBackButton(
        title = stringResource(id = R.string.cart_settings) ,
        onBackClicked = { activity.finish() }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues) ,
        ) {
            item {
                PreferenceCategoryItem(title = stringResource(id = R.string.shopping_cart))
                PreferenceItem(title = stringResource(id = R.string.currency) ,
                               summary = stringResource(id = R.string.summary_preference_settings_currency) ,
                               onClick = { showDialog.value = true })
            }
            item {
                SwitchPreferenceItem(title = stringResource(id = R.string.open_carts_after_creation) ,
                                     summary = stringResource(id = R.string.summary_preference_settings_open_carts_after_creation) ,
                                     checked = openCartsAfterCreation ,
                                     onCheckedChange = { isChecked ->
                                         coroutineScope.launch {
                                             dataStore.saveOpenCartsAfterCreation(isChecked)
                                         }
                                     })
            }
        }
    }
    if (showDialog.value) {
        SelectCurrencyAlertDialog(dataStore = dataStore ,
                                  onDismiss = { showDialog.value = false } ,
                                  onCurrencySelected = {
                                      showDialog.value = false
                                  })
    }
}