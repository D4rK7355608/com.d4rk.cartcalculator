package com.d4rk.cartcalculator.ui.components.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.data.model.ui.navigation.NavigationDrawerItem
import com.d4rk.cartcalculator.data.model.ui.screens.MainScreenState
import com.d4rk.cartcalculator.data.model.ui.screens.UiMainScreen
import com.d4rk.cartcalculator.ui.screens.home.HomeScreen
import com.d4rk.cartcalculator.ui.screens.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun LeftNavigationRail(
    mainScreenState : MainScreenState , paddingValues : PaddingValues , coroutineScope : CoroutineScope , isRailExpanded : Boolean , viewModel : HomeViewModel , snackbarHostState : SnackbarHostState
) {
    val uiState : UiMainScreen by mainScreenState.viewModel.uiState.collectAsState()

    val drawerItems : List<NavigationDrawerItem> = uiState.navigationDrawerItems

    val railWidth : Dp by animateDpAsState(
        targetValue = if (isRailExpanded) 200.dp else 72.dp , animationSpec = tween(durationMillis = 300)
    )

    Row(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
        NavigationRail(
            modifier = Modifier.width(width = railWidth)
        ) {
            drawerItems.forEach { item ->
                NavigationRailItem(selected = false , onClick = {
                    handleNavigationItemClick(
                        context = mainScreenState.context , item = item , drawerState = mainScreenState.drawerState , coroutineScope = coroutineScope
                    )
                } , icon = {
                    Icon(
                        imageVector = item.selectedIcon , contentDescription = stringResource(id = item.title)
                    )
                } , label = if (isRailExpanded) {
                    { Text(text = stringResource(id = item.title)) }
                }
                else null)
            }
        }

        Box(modifier = Modifier.weight(weight = 1f)) {
            HomeScreen(
                context = mainScreenState.context , view = mainScreenState.view , viewModel = viewModel , snackbarHostState = snackbarHostState
            )
        }
    }
}