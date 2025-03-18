package com.d4rk.cartcalculator.app.main.ui

import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.AnimatedExtendedFloatingActionButton
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.SmallFloatingActionButton
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.StatusSnackbarHost
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.main.domain.model.UiMainScreen
import com.d4rk.cartcalculator.app.main.ui.components.navigation.MainTopAppBar
import com.d4rk.cartcalculator.app.main.ui.components.navigation.NavigationDrawer
import com.d4rk.cartcalculator.app.main.ui.components.navigation.NavigationHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {
    val viewModel : MainViewModel = koinViewModel()
    val screenState : UiStateScreen<UiMainScreen> by viewModel.screenState.collectAsState()

    NavigationDrawer(screenState = screenState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldContent(drawerState : DrawerState) {
    val scrollBehavior : TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackBarHostState : SnackbarHostState = remember { SnackbarHostState() }
    val isFabExtended : MutableState<Boolean> = remember { mutableStateOf(value = true) }
    val isFabVisible : MutableState<Boolean> = remember { mutableStateOf(value = false) }
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val navController : NavHostController = rememberNavController()
    val view : View = LocalView.current

    LaunchedEffect(scrollBehavior.state.contentOffset) {
        isFabExtended.value = scrollBehavior.state.contentOffset >= 0f
    }

    Scaffold(modifier = Modifier
            .imePadding()
            .nestedScroll(connection = scrollBehavior.nestedScrollConnection) , topBar = {
        MainTopAppBar(navigationIcon = if (drawerState.isOpen) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = { coroutineScope.launch { drawerState.open() } } , scrollBehavior = scrollBehavior)
    } , snackbarHost = {
        StatusSnackbarHost(snackBarHostState = snackBarHostState , view = view , navController = navController)
    } , floatingActionButton = {
        Column(horizontalAlignment = Alignment.End) {
            SmallFloatingActionButton(modifier = Modifier.padding(bottom = SizeConstants.MediumSize) , isVisible = isFabVisible.value , isExtended = isFabExtended.value , icon = Icons.Outlined.ImportExport , onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set("toggleImportDialog" , true)
            })

            AnimatedExtendedFloatingActionButton(visible = isFabVisible.value , onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set("openNewCartDialog" , true)
            } , text = { Text(stringResource(R.string.add_new_cart)) } , icon = { Icon(Icons.Outlined.AddShoppingCart , null) } , modifier = Modifier.bounceClick() , expanded = isFabExtended.value)
        }
    }) { paddingValues ->
        NavigationHost(navController = navController , snackbarHostState = snackBarHostState , onFabVisibilityChanged = { isFabVisible.value = it } , paddingValues = paddingValues)
    }
}