package com.d4rk.cartcalculator.ui.screens.settings.privacy.ads

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.android.libs.apptoolkit.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.android.libs.apptoolkit.ui.components.preferences.PreferenceItem
import com.d4rk.android.libs.apptoolkit.ui.components.preferences.SwitchCardComposable
import com.d4rk.cartcalculator.BuildConfig
import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.datastore.DataStore
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdsSettingsScreen(activity : AdsSettingsActivity) {
    val context : Context = LocalContext.current
    val dataStore : DataStore = AppCoreManager.dataStore
    val switchState : State<Boolean> = dataStore.ads.collectAsState(initial = ! BuildConfig.DEBUG)
    val coroutineScope : CoroutineScope = rememberCoroutineScope()

    LargeTopAppBarWithScaffold(title = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.ads) , onBackClicked = { activity.finish() }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues) ,
            ) {
                item(key = "display_ads") {
                    SwitchCardComposable(
                        title = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.display_ads) , switchState = switchState
                    ) { isChecked ->
                        coroutineScope.launch {
                            dataStore.saveAds(isChecked = isChecked)
                        }
                    }
                }
                item {
                    Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                        PreferenceItem(title = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.personalized_ads) , enabled = switchState.value , summary = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.summary_ads_personalized_ads) , onClick = {
                            val params : ConsentRequestParameters = ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()
                            val consentInformation : ConsentInformation = UserMessagingPlatform.getConsentInformation(
                                context
                            )
                            consentInformation.requestConsentInfoUpdate(activity , params , {
                                activity.openForm()
                            } , {})
                        })
                    }
                }

                item {
                    Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 24.dp)) {
                        InfoMessageSection(
                            message = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.summary_ads) , learnMoreText = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.learn_more) , learnMoreUrl = "https://sites.google.com/view/d4rk7355608/more/apps/ads-help-center"
                        )
                    }
                }
            }
        }
    }
}