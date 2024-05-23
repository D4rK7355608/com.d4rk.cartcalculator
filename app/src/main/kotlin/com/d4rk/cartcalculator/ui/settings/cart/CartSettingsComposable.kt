package com.d4rk.cartcalculator.ui.settings.cart

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.store.DataStore
import com.d4rk.cartcalculator.dialogs.CurrencyDialog
import com.d4rk.cartcalculator.utils.PreferenceCategoryItem
import com.d4rk.cartcalculator.utils.PreferenceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartSettingsComposable(activity: CartSettingsActivity) {
    val context = LocalContext.current
    val dataStore = DataStore.getInstance(context)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val showDialog = remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.cart_settings)) }, navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }, scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues),
        ) {
            item {
                PreferenceCategoryItem(title = stringResource(R.string.shopping_cart))
                PreferenceItem(title = stringResource(R.string.currency),
                    summary = stringResource(id = R.string.summary_preference_settings_currency),
                    onClick = { showDialog.value = true })
            }
        }
    }

    if (showDialog.value) {
        CurrencyDialog(dataStore = dataStore,
            onDismiss = { showDialog.value = false },
            onCurrencySelected = {
                showDialog.value = false
            })
    }
}