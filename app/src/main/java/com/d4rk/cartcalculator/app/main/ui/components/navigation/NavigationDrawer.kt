package com.d4rk.cartcalculator.app.main.ui.components.navigation

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.dialogs.ChangelogDialog
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.NavigationDrawerItemContent
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.BuildInfoProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.hapticDrawerSwipe
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.cartcalculator.app.cart.list.domain.model.ui.UiHomeData
import com.d4rk.cartcalculator.app.cart.list.ui.HomeViewModel
import com.d4rk.cartcalculator.app.main.domain.model.MainScreenState
import com.d4rk.cartcalculator.app.main.ui.MainScaffoldContent
import kotlinx.coroutines.CoroutineScope
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun NavigationDrawer(
    mainScreenState: MainScreenState,
    homeViewModel: HomeViewModel,
    homeScreenState: UiStateScreen<UiHomeData>
) {
    val drawerState : DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val context : Context = LocalContext.current
    val changelogUrl: String = koinInject(qualifier = named("github_changelog"))
    val buildInfoProvider: BuildInfoProvider = koinInject()
    var showChangelog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        modifier = Modifier.hapticDrawerSwipe(state = drawerState),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                LargeVerticalSpacer()
                mainScreenState.uiState.navigationDrawerItems.forEach { item ->
                    NavigationDrawerItemContent(item = item, handleNavigationItemClick = {
                        handleNavigationItemClick(
                            context = context,
                            item = item,
                            drawerState = drawerState,
                            coroutineScope = coroutineScope,
                            onChangelogRequested = { showChangelog = true }
                        )
                    })
                }
            }
        }) {
        MainScaffoldContent(
            drawerState = drawerState,
            mainScreenState = mainScreenState,
            homeViewModel = homeViewModel,
            homeScreenState = homeScreenState
        )
    }

    if (showChangelog) {
        ChangelogDialog(
            changelogUrl = changelogUrl,
            buildInfoProvider = buildInfoProvider,
            onDismiss = { showChangelog = false }
        )
    }
}