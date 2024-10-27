package com.d4rk.cartcalculator.ui.screens.settings.cart

import android.content.Context
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.datastore.DataStore
import com.d4rk.cartcalculator.ui.components.PreferenceCategoryItem
import com.d4rk.cartcalculator.ui.components.PreferenceItem
import com.d4rk.cartcalculator.ui.components.dialogs.SelectCurrencyAlertDialog
import com.d4rk.cartcalculator.ui.components.navigation.TopAppBarScaffoldWithBackButton

@Composable
fun CartSettingsComposable(activity : CartSettingsActivity) {
    val context : Context = LocalContext.current
    val dataStore = DataStore.getInstance(context)
    val showDialog = remember { mutableStateOf(value = false) }
    TopAppBarScaffoldWithBackButton(title = stringResource(id = R.string.cart_settings) ,
                                    onBackClicked = { activity.finish() }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues) ,
        ) {
            item {
                PreferenceCategoryItem(title = stringResource(R.string.shopping_cart))
                PreferenceItem(title = stringResource(R.string.currency) ,
                               summary = stringResource(id = R.string.summary_preference_settings_currency) ,
                               onClick = { showDialog.value = true })
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