package com.d4rk.cartcalculator.ui.screens.main

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.d4rk.android.libs.apptoolkit.ui.components.buttons.AnimatedExtendedFloatingActionButton
import com.d4rk.android.libs.apptoolkit.utils.helpers.ScreenHelper
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.datastore.DataStore
import com.d4rk.cartcalculator.data.model.ui.screens.MainScreenState
import com.d4rk.cartcalculator.ui.components.navigation.LeftNavigationRail
import com.d4rk.cartcalculator.ui.components.navigation.NavigationDrawer
import com.d4rk.cartcalculator.ui.components.navigation.TopAppBarMain
import com.d4rk.cartcalculator.ui.screens.home.HomeScreen
import com.d4rk.cartcalculator.ui.screens.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel : MainViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navController = rememberNavController()
    val context = LocalContext.current
    val view : View = LocalView.current
    val dataStore : DataStore = AppCoreManager.dataStore

    val isTabletOrLandscape : Boolean = ScreenHelper.isLandscapeOrTablet(context = context)
    val snackbarHostState : SnackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val homeViewModel : HomeViewModel = viewModel()

    val isFabVisible : Boolean by homeViewModel.isFabVisible.collectAsState()
    val isFabExtended : MutableState<Boolean> = remember { mutableStateOf(value = true) }

    LaunchedEffect(key1 = scrollBehavior.state.contentOffset) {
        isFabExtended.value = scrollBehavior.state.contentOffset >= 0f
    }
    val mainScreenState = remember {
        MainScreenState(
            context = context , view = view , drawerState = drawerState , navHostController = navController , dataStore = dataStore , viewModel = viewModel
        )
    }

    if (isTabletOrLandscape) {
        MainScaffoldTabletContent(mainScreenState = mainScreenState , isFabVisible = isFabVisible , homeViewModel = homeViewModel , snackbarHostState = snackbarHostState , isFabExtended = isFabExtended.value, scrollBehavior = scrollBehavior)
    }
    else {
        NavigationDrawer(mainScreenState = mainScreenState , isFabVisible = isFabVisible , homeViewModel = homeViewModel , snackbarHostState = snackbarHostState , isFabExtended = isFabExtended.value, scrollBehavior = scrollBehavior)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldContent(mainScreenState : MainScreenState , coroutineScope : CoroutineScope , isFabVisible : Boolean , viewModel : HomeViewModel , snackbarHostState : SnackbarHostState , isFabExtended : Boolean, scrollBehavior : TopAppBarScrollBehavior) {
    Scaffold(modifier = Modifier.imePadding().nestedScroll(scrollBehavior.nestedScrollConnection) , floatingActionButton = {
        AnimatedExtendedFloatingActionButton(visible = isFabVisible , onClick = {
            mainScreenState.view.playSoundEffect(SoundEffectConstants.CLICK)
            viewModel.openNewCartDialog()
        } , text = { Text(text = stringResource(id = R.string.add_new_cart)) } , icon = {
            Icon(
                Icons.Outlined.AddShoppingCart , contentDescription = null
            )
        } , modifier = Modifier , expanded = isFabExtended)
    } , snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    } , topBar = {
        TopAppBarMain(view = mainScreenState.view , context = mainScreenState.context , navigationIcon = if (mainScreenState.drawerState.isOpen) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = {
            coroutineScope.launch {
                mainScreenState.drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        }, scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        HomeScreen(
            context = mainScreenState.context , view = mainScreenState.view , viewModel = viewModel , snackbarHostState = snackbarHostState , paddingValues = paddingValues
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldTabletContent(mainScreenState : MainScreenState , isFabVisible : Boolean , homeViewModel : HomeViewModel , snackbarHostState : SnackbarHostState , isFabExtended : Boolean, scrollBehavior : TopAppBarScrollBehavior) {

    var isRailExpanded : Boolean by remember { mutableStateOf(value = false) }
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val context : Context = LocalContext.current

    Scaffold(contentWindowInsets = WindowInsets.safeContent , modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection) , floatingActionButton = {
        AnimatedExtendedFloatingActionButton(
            visible = isFabVisible ,
            onClick = {
                mainScreenState.view.playSoundEffect(SoundEffectConstants.CLICK)
                homeViewModel.openNewCartDialog()
            } ,
            text = { Text(text = stringResource(id = R.string.add_new_cart)) } ,
            icon = {
                Icon(
                    Icons.Outlined.AddShoppingCart , contentDescription = null
                )
            } ,
            modifier = Modifier , expanded = isFabExtended ,
        )
    } , snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    } , topBar = {
        TopAppBarMain(view = mainScreenState.view , context = context , navigationIcon = if (isRailExpanded) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = {
            isRailExpanded = ! isRailExpanded
        }, scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        LeftNavigationRail(
            coroutineScope = coroutineScope , mainScreenState = mainScreenState , paddingValues = paddingValues , isRailExpanded = isRailExpanded , viewModel = homeViewModel , snackbarHostState = snackbarHostState
        )
    }
}