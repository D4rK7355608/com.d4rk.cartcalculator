package com.d4rk.cartcalculator.app.settings.cart.list.ui

import android.content.Context
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.app.settings.general.ui.GeneralSettingsActivity
import com.d4rk.android.libs.apptoolkit.core.ui.components.preferences.PreferenceCategoryItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.preferences.SettingsPreferenceItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.preferences.SwitchPreferenceItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ExtraTinyVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.cart.list.domain.model.SortOption
import com.d4rk.cartcalculator.app.settings.cart.list.ui.dialogs.SelectCurrencyAlertDialog
import com.d4rk.cartcalculator.app.settings.cart.list.ui.dialogs.SortOrderDialog
import com.d4rk.cartcalculator.app.settings.settings.utils.constants.SettingsConstants
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun CartSettingsList(paddingValues : PaddingValues) {
    val dataStore : DataStore = koinInject()
    val showDialog = remember { mutableStateOf(value = false) }
    val openCartsAfterCreation by dataStore.openCartsAfterCreation.collectAsState(initial = true)
    val currentSort by dataStore.sortOption.collectAsState(initial = SortOption.DEFAULT)
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val context : Context = LocalContext.current
    var showSortDialog by remember { mutableStateOf(false) }

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

        item {
            SmallVerticalSpacer()
            PreferenceCategoryItem(title = stringResource(id = R.string.backup_carts_title))
            SmallVerticalSpacer()
            Column(
                modifier = Modifier
                    .padding(horizontal = SizeConstants.LargeSize)
                    .clip(shape = RoundedCornerShape(size = SizeConstants.LargeSize))
            ) {
                SettingsPreferenceItem(
                    title = stringResource(id = R.string.preference_title_cart_backup),
                    summary = stringResource(id = R.string.preference_summary_cart_backup),
                    onClick = {
                        GeneralSettingsActivity.start(
                            context = context,
                            title = context.getString(R.string.backup_carts_title),
                            contentKey = SettingsConstants.KEY_SETTINGS_BACKUP_CARTS
                        )
                    }
                )
            }
        }
    }

    if (showSortDialog) {
        SortOrderDialog(current = currentSort, onDismiss = { showSortDialog = false }) { option ->
            showSortDialog = false
            coroutineScope.launch {
                dataStore.saveSortOption(option)
            }
        }
    }
}