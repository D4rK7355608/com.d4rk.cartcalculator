package com.d4rk.cartcalculator.app.main.ui.components.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.cartcalculator.app.main.domain.model.UiMainScreen
import com.d4rk.cartcalculator.app.main.ui.MainScaffoldContent
import kotlinx.coroutines.CoroutineScope

@Composable
fun NavigationDrawer(screenState : UiStateScreen<UiMainScreen>) {
    val drawerState : DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val uiState : UiMainScreen = screenState.data ?: UiMainScreen()

    ModalNavigationDrawer(drawerState = drawerState , drawerContent = {
        ModalDrawerSheet {
            LargeVerticalSpacer()
            uiState.navigationDrawerItems.forEach { item ->
                NavigationDrawerItemContent(item = item , drawerState = drawerState)
            }
        }
    }) {
        MainScaffoldContent(drawerState = drawerState)
    }
}

@Composable
private fun NavigationDrawerItemContent(item : NavigationDrawerItem , drawerState : DrawerState) {
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val context : Context = LocalContext.current
    val title = stringResource(id = item.title)

    NavigationDrawerItem(label = { Text(text = title) } , selected = false , onClick = {
        handleNavigationItemClick(context = context , item = item , drawerState = drawerState , coroutineScope = coroutineScope)
    } , icon = {
        Icon(item.selectedIcon , contentDescription = title)
    } , badge = {
        if (item.badgeText.isNotBlank()) {
            Text(text = item.badgeText)
        }
    } , modifier = Modifier
            .padding(paddingValues = NavigationDrawerItemDefaults.ItemPadding)
            .bounceClick())
}