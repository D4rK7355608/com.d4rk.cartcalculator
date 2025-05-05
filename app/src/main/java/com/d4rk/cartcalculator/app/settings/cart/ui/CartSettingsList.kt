package com.d4rk.cartcalculator.app.settings.cart.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.preferences.PreferenceCategoryItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.preferences.SettingsPreferenceItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.preferences.SwitchPreferenceItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ExtraTinyVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.settings.cart.ui.components.dialogs.SelectCurrencyAlertDialog
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun CartSettingsList(paddingValues : PaddingValues) {
    val dataStore : DataStore = koinInject()
    val showDialog = remember { mutableStateOf(value = false) }
    val openCartsAfterCreation by dataStore.openCartsAfterCreation.collectAsState(initial = true)
    val coroutineScope = rememberCoroutineScope()

    if (showDialog.value) {
        Box(modifier = Modifier.padding(paddingValues)) {
            SelectCurrencyAlertDialog(dataStore = dataStore , onDismiss = { showDialog.value = false } , onCurrencySelected = {
                showDialog.value = false
            })
        }
    }
    LazyColumn(
        modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues) ,
    ) {
        item {
            PreferenceCategoryItem(title = stringResource(id = R.string.shopping_cart))
            SmallVerticalSpacer()
            Column(
                modifier = Modifier
                        .padding(horizontal = SizeConstants.LargeSize)
                        .clip(shape = RoundedCornerShape(size = SizeConstants.LargeSize))
            ) {
                SettingsPreferenceItem(title = stringResource(id = R.string.currency) , summary = stringResource(id = R.string.summary_preference_settings_currency) , onClick = { showDialog.value = true })
                ExtraTinyVerticalSpacer()
                SwitchPreferenceItem(title = stringResource(id = R.string.open_carts_after_creation) , summary = stringResource(id = R.string.summary_preference_settings_open_carts_after_creation) , checked = openCartsAfterCreation , onCheckedChange = { isChecked ->
                    coroutineScope.launch {
                        dataStore.saveOpenCartsAfterCreation(isChecked)
                    }
                })
            }
        }
    }
}