package com.d4rk.cartcalculator.app.main.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.LeftNavigationRail
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.MainTopAppBar
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.fab.AnimatedExtendedFloatingActionButton
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.fab.SmallFloatingActionButton
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHost
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ScreenHelper
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.cart.list.domain.model.ui.UiHomeData
import com.d4rk.cartcalculator.app.cart.list.ui.HomeScreen
import com.d4rk.cartcalculator.app.cart.list.ui.HomeViewModel
import com.d4rk.cartcalculator.app.main.domain.model.MainScreenState
import com.d4rk.cartcalculator.app.main.domain.model.UiMainScreen
import com.d4rk.cartcalculator.app.main.ui.components.navigation.AppNavigationHost
import com.d4rk.cartcalculator.app.main.ui.components.navigation.NavigationDrawer
import com.d4rk.cartcalculator.app.main.ui.components.navigation.handleNavigationItemClick
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel : MainViewModel = koinViewModel()
    val screenState : UiStateScreen<UiMainScreen> by viewModel.screenState.collectAsState()
    val context : Context = LocalContext.current
    val isTabletOrLandscape : Boolean = ScreenHelper.isLandscapeOrTablet(context = context)

    val mainScreenState = MainScreenState(
        navController = rememberNavController() ,
        isFabVisible = remember { mutableStateOf(value = false) } ,
        isFabExtended = remember { mutableStateOf(value = true) } ,
        snackbarHostState = remember { SnackbarHostState() } ,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior() ,
        coroutineScope = rememberCoroutineScope() ,
        mainViewModel = viewModel ,
        uiState = screenState.data ?: UiMainScreen())

    val homeViewModel : HomeViewModel = koinViewModel()
    val homeScreenState : UiStateScreen<UiHomeData> by homeViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = mainScreenState.scrollBehavior.state.contentOffset) {
        mainScreenState.isFabExtended.value = mainScreenState.scrollBehavior.state.contentOffset >= 0f
    }

    if (isTabletOrLandscape) {
        MainScaffoldTabletContent(mainScreenState = mainScreenState , homeViewModel = homeViewModel , homeScreenState = homeScreenState)
    }
    else {
        NavigationDrawer(mainScreenState = mainScreenState , homeViewModel = homeViewModel , homeScreenState = homeScreenState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldContent(drawerState : DrawerState , mainScreenState : MainScreenState , homeViewModel : HomeViewModel , homeScreenState : UiStateScreen<UiHomeData>) {
    Scaffold(modifier = Modifier
            .imePadding()
            .nestedScroll(connection = mainScreenState.scrollBehavior.nestedScrollConnection) , topBar = {
        MainTopAppBar(navigationIcon = if (drawerState.isOpen) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = { mainScreenState.coroutineScope.launch { drawerState.open() } } , scrollBehavior = mainScreenState.scrollBehavior)
    } , snackbarHost = {
        DefaultSnackbarHost(snackbarState = mainScreenState.snackbarHostState)
    } , floatingActionButton = {
        Column(horizontalAlignment = Alignment.End) {
            SmallFloatingActionButton(modifier = Modifier.padding(bottom = SizeConstants.MediumSize) , isVisible = mainScreenState.isFabVisible.value , isExtended = mainScreenState.isFabExtended.value , icon = Icons.Outlined.ImportExport , onClick = {
                mainScreenState.navController.currentBackStackEntry?.savedStateHandle?.set("toggleImportDialog" , true)
            })

            AnimatedExtendedFloatingActionButton(visible = mainScreenState.isFabVisible.value , onClick = {
                mainScreenState.navController.currentBackStackEntry?.savedStateHandle?.set("openNewCartDialog" , true)
            } , text = { Text(text = stringResource(id = R.string.add_new_cart)) } , icon = { Icon(imageVector = Icons.Outlined.EditCalendar , contentDescription = null) } , modifier = Modifier.bounceClick() , expanded = mainScreenState.isFabExtended.value)
        }
    }) { paddingValues : PaddingValues ->
        AppNavigationHost(navController = mainScreenState.navController , snackbarHostState = mainScreenState.snackbarHostState , onFabVisibilityChanged = { mainScreenState.isFabVisible.value = it } , paddingValues = paddingValues , homeViewModel = homeViewModel , homeScreenState = homeScreenState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldTabletContent(mainScreenState : MainScreenState , homeViewModel : HomeViewModel , homeScreenState : UiStateScreen<UiHomeData>) {
    var isRailExpanded : Boolean by remember { mutableStateOf(value = false) }
    val context : Context = LocalContext.current
    val navBackStackEntry : NavBackStackEntry? by mainScreenState.navController.currentBackStackEntryAsState()
    val currentRoute : String? = navBackStackEntry?.destination?.route

    Scaffold(modifier = Modifier.fillMaxSize() , topBar = {
        MainTopAppBar(navigationIcon = if (isRailExpanded) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = {
            mainScreenState.coroutineScope.launch { isRailExpanded = ! isRailExpanded }
        } , scrollBehavior = mainScreenState.scrollBehavior)
    } , floatingActionButton = {
        Column(horizontalAlignment = Alignment.End) {
            SmallFloatingActionButton(modifier = Modifier.padding(bottom = SizeConstants.MediumSize) , isVisible = mainScreenState.isFabVisible.value , isExtended = mainScreenState.isFabExtended.value , icon = Icons.Outlined.ImportExport , onClick = {
                mainScreenState.navController.currentBackStackEntry?.savedStateHandle?.set("toggleImportDialog" , true)
            })

            AnimatedExtendedFloatingActionButton(visible = mainScreenState.isFabVisible.value , onClick = {
                mainScreenState.navController.currentBackStackEntry?.savedStateHandle?.set("openNewCartDialog" , true)
            } , text = { Text(text = stringResource(id = R.string.add_new_cart)) } , icon = { Icon(imageVector = Icons.Outlined.EditCalendar , contentDescription = null) } , modifier = Modifier.bounceClick() , expanded = mainScreenState.isFabExtended.value)
        }
    }) { paddingValues : PaddingValues ->
        LeftNavigationRail(drawerItems = mainScreenState.uiState.navigationDrawerItems , currentRoute = currentRoute , isRailExpanded = isRailExpanded , paddingValues = paddingValues , onDrawerItemClick = { item : NavigationDrawerItem ->
            handleNavigationItemClick(context = context , item = item)
        } , content = {
            HomeScreen(paddingValues = paddingValues , viewModel = homeViewModel , onFabVisibilityChanged = {
                mainScreenState.isFabVisible.value = it
            } , snackbarHostState = mainScreenState.snackbarHostState , screenState = homeScreenState)
        })
    }
}