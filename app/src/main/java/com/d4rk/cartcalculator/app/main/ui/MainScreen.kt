package com.d4rk.cartcalculator.app.main.ui

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.LeftNavigationRail
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.StatusSnackbarHost
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ScreenHelper
import com.d4rk.cartcalculator.app.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.home.ui.HomeScreen
import com.d4rk.cartcalculator.app.home.ui.HomeViewModel
import com.d4rk.cartcalculator.app.main.domain.model.UiMainScreen
import com.d4rk.cartcalculator.app.main.ui.components.navigation.AppNavigationHost
import com.d4rk.cartcalculator.app.main.ui.components.navigation.NavigationDrawer
import com.d4rk.cartcalculator.app.main.ui.components.navigation.handleNavigationItemClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun MainScreen() {
    val viewModel : MainViewModel = koinViewModel()
    val screenState : UiStateScreen<UiMainScreen> by viewModel.screenState.collectAsState()
    val context : Context = LocalContext.current
    val isTabletOrLandscape : Boolean = ScreenHelper.isLandscapeOrTablet(context = context)

    if (isTabletOrLandscape) {
        MainScaffoldTabletContent()
    }
    else {
        NavigationDrawer(screenState = screenState)
    }
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

    LaunchedEffect(key1 = scrollBehavior.state.contentOffset) {
        isFabExtended.value = scrollBehavior.state.contentOffset >= 0f
    }

    Scaffold(modifier = Modifier
            .imePadding()
            .nestedScroll(connection = scrollBehavior.nestedScrollConnection) , topBar = {
        com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.MainTopAppBar(navigationIcon = if (drawerState.isOpen) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = { coroutineScope.launch { drawerState.open() } } , scrollBehavior = scrollBehavior)
    } , snackbarHost = {
        StatusSnackbarHost(snackBarHostState = snackBarHostState , view = view , navController = navController)
    }) { paddingValues : PaddingValues ->
        AppNavigationHost(navController = navController , snackbarHostState = snackBarHostState , onFabVisibilityChanged = { isFabVisible.value = it } , paddingValues = paddingValues)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldTabletContent() {
    val scrollBehavior : TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var isRailExpanded : Boolean by remember { mutableStateOf(value = false) }
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val context : Context = LocalContext.current
    val viewModel : MainViewModel = koinViewModel()
    val screenState : UiStateScreen<UiMainScreen> by viewModel.screenState.collectAsState()
    val uiState : UiMainScreen = screenState.data ?: UiMainScreen()
    val navController : NavHostController = rememberNavController()
    val navBackStackEntry : NavBackStackEntry? by navController.currentBackStackEntryAsState()
    val currentRoute : String? = navBackStackEntry?.destination?.route
    val snackbarHostState : SnackbarHostState = remember { SnackbarHostState() }
    val isFabExtended : MutableState<Boolean> = remember { mutableStateOf(value = true) }
    val isFabVisible : MutableState<Boolean> = remember { mutableStateOf(value = false) }

    LaunchedEffect(key1 = scrollBehavior.state.contentOffset) {
        isFabExtended.value = scrollBehavior.state.contentOffset >= 0f
    }

    Scaffold(
        modifier = Modifier.fillMaxSize() , topBar = {
            com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.MainTopAppBar(
                navigationIcon = if (isRailExpanded) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = {
                    coroutineScope.launch { isRailExpanded = ! isRailExpanded }
                } , scrollBehavior = scrollBehavior
            )
        }) { paddingValues : PaddingValues ->
        LeftNavigationRail(drawerItems = uiState.navigationDrawerItems , currentRoute = currentRoute , isRailExpanded = isRailExpanded , paddingValues = paddingValues , onDrawerItemClick = { item : NavigationDrawerItem ->
            handleNavigationItemClick(context = context , item = item)
        } , content = {
            val homeViewModel : HomeViewModel = koinViewModel()
            val homeScreenState : UiStateScreen<UiHomeData> by homeViewModel.screenState.collectAsState()

            HomeScreen(
                paddingValues = paddingValues , viewModel = homeViewModel , onFabVisibilityChanged = {
                    isFabVisible.value = it
                } , snackbarHostState = snackbarHostState , screenState = homeScreenState
            )
        })
    }
}