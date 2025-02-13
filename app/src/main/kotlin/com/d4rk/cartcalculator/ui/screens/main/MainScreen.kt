package com.d4rk.cartcalculator.ui.screens.main

import android.content.Context
import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.d4rk.android.libs.apptoolkit.ui.components.buttons.AnimatedExtendedFloatingActionButton
import com.d4rk.android.libs.apptoolkit.utils.helpers.ScreenHelper
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.model.ui.screens.MainScreenState
import com.d4rk.cartcalculator.ui.components.navigation.LeftNavigationRail
import com.d4rk.cartcalculator.ui.components.navigation.NavigationDrawer
import com.d4rk.cartcalculator.ui.components.navigation.TopAppBarMain
import com.d4rk.cartcalculator.ui.screens.home.HomeScreen
import com.d4rk.cartcalculator.ui.screens.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel : MainViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navController = rememberNavController()
    val context = LocalContext.current
    val view = LocalView.current
    val dataStore = AppCoreManager.dataStore

    val isTabletOrLandscape : Boolean = ScreenHelper.isLandscapeOrTablet(context = context)
    val snackbarHostState : SnackbarHostState = remember { SnackbarHostState() }


    val homeViewModel : HomeViewModel = viewModel()
    val isFabVisible : Boolean by homeViewModel.isFabVisible.collectAsState()

    val mainScreenState = remember {
        MainScreenState(
            context = context , view = view , drawerState = drawerState , navHostController = navController , dataStore = dataStore , viewModel = viewModel
        )
    }

    if (isTabletOrLandscape) {
        MainScaffoldTabletContent(mainScreenState = mainScreenState , isFabVisible = isFabVisible , homeViewModel = homeViewModel , snackbarHostState = snackbarHostState)
    }
    else {
        NavigationDrawer(
            mainScreenState = mainScreenState , isFabVisible = isFabVisible , homeViewModel = homeViewModel , snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun MainScaffoldContent(
    mainScreenState : MainScreenState , coroutineScope : CoroutineScope , isFabVisible : Boolean , viewModel : HomeViewModel , snackbarHostState : SnackbarHostState
) {
    Scaffold(modifier = Modifier.imePadding() , floatingActionButton = {
        AnimatedExtendedFloatingActionButton(visible = isFabVisible , onClick = {
            mainScreenState.view.playSoundEffect(SoundEffectConstants.CLICK)
            viewModel.openNewCartDialog()
        } , text = { Text(text = stringResource(id = R.string.add_new_cart)) } , icon = {
            Icon(
                Icons.Outlined.AddShoppingCart , contentDescription = null
            )
        } , modifier = Modifier)
    } , topBar = {

        TopAppBarMain(view = mainScreenState.view , context = mainScreenState.context , navigationIcon = if (mainScreenState.drawerState.isOpen) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = {
            coroutineScope.launch {
                mainScreenState.drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        })
    }) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            HomeScreen(
                context = mainScreenState.context , view = mainScreenState.view , viewModel = viewModel , snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
fun MainScaffoldTabletContent(mainScreenState : MainScreenState , isFabVisible : Boolean , homeViewModel : HomeViewModel , snackbarHostState : SnackbarHostState) {
    var isRailExpanded : Boolean by remember { mutableStateOf(value = false) }
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val context : Context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize() , floatingActionButton = {
        AnimatedExtendedFloatingActionButton(visible = isFabVisible , onClick = {
            mainScreenState.view.playSoundEffect(SoundEffectConstants.CLICK)
            homeViewModel.openNewCartDialog()
        } , text = { Text(text = stringResource(id = R.string.add_new_cart)) } , icon = {
            Icon(
                Icons.Outlined.AddShoppingCart , contentDescription = null
            )
        } , modifier = Modifier)
    } , topBar = {
        TopAppBarMain(view = mainScreenState.view , context = context , navigationIcon = if (isRailExpanded) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = {
            isRailExpanded = ! isRailExpanded
        })
    }) { paddingValues ->
        LeftNavigationRail(
            coroutineScope = coroutineScope , mainScreenState = mainScreenState , paddingValues = paddingValues , isRailExpanded = isRailExpanded , viewModel = homeViewModel , snackbarHostState = snackbarHostState
        )
    }
}