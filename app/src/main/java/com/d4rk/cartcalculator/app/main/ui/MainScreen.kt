package com.d4rk.cartcalculator.app.main.ui

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.dialogs.ChangelogDialog
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.LeftNavigationRail
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.BuildInfoProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHost
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ScreenHelper
import com.d4rk.cartcalculator.app.cart.list.domain.model.ui.UiHomeData
import com.d4rk.cartcalculator.app.cart.list.ui.HomeViewModel
import com.d4rk.cartcalculator.app.main.domain.model.MainScreenState
import com.d4rk.cartcalculator.app.main.domain.model.UiMainScreen
import com.d4rk.cartcalculator.app.main.ui.components.FloatingActionButtonsColumn
import com.d4rk.cartcalculator.app.main.ui.components.navigation.AppNavigationHost
import com.d4rk.cartcalculator.app.main.ui.components.navigation.MainTopAppBar
import com.d4rk.cartcalculator.app.main.ui.components.navigation.NavigationDrawer
import com.d4rk.cartcalculator.app.main.ui.components.navigation.handleNavigationItemClick
import com.d4rk.cartcalculator.app.main.utils.constants.NavigationRoutes
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel: MainViewModel = koinViewModel()
    val screenState: UiStateScreen<UiMainScreen> by viewModel.screenState.collectAsState()
    val context: Context = LocalContext.current
    val isTabletOrLandscape: Boolean = ScreenHelper.isLandscapeOrTablet(context = context)

    val mainScreenState = MainScreenState(
        navController = rememberNavController(),
        isFabVisible = remember { mutableStateOf(value = false) },
        isFabExtended = remember { mutableStateOf(value = true) },
        snackbarHostState = remember { SnackbarHostState() },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        coroutineScope = rememberCoroutineScope(),
        mainViewModel = viewModel,
        uiState = screenState.data ?: UiMainScreen())

    val homeViewModel: HomeViewModel = koinViewModel()
    val homeScreenState: UiStateScreen<UiHomeData> by homeViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = mainScreenState.scrollBehavior.state.contentOffset) {
        mainScreenState.isFabExtended.value =
            mainScreenState.scrollBehavior.state.contentOffset >= 0f
    }

    if (isTabletOrLandscape) {
        MainScaffoldTabletContent(
            mainScreenState = mainScreenState,
            homeViewModel = homeViewModel,
            homeScreenState = homeScreenState
        )
    } else {
        NavigationDrawer(
            mainScreenState = mainScreenState,
            homeViewModel = homeViewModel,
            homeScreenState = homeScreenState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldContent(
    drawerState: DrawerState,
    mainScreenState: MainScreenState,
    homeViewModel: HomeViewModel,
    homeScreenState: UiStateScreen<UiHomeData>
) {
    val navBackStackEntry: NavBackStackEntry? by mainScreenState.navController.currentBackStackEntryAsState()
    val currentRoute: String? = navBackStackEntry?.destination?.route
    val isSearchScreen: Boolean =
        currentRoute?.startsWith(NavigationRoutes.ROUTE_SEARCH.substringBefore(delimiter = "/{")) == true

    var currentSearchQuery by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .imePadding()
            .nestedScroll(connection = mainScreenState.scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopAppBar(
                navigationIcon = if (isSearchScreen) Icons.AutoMirrored.Filled.ArrowBack else if (drawerState.isOpen) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu,
                onNavigationIconClick = {
                    if (isSearchScreen) {
                        mainScreenState.navController.popBackStack()
                    } else {
                        mainScreenState.coroutineScope.launch { drawerState.open() }
                    }
                },
                scrollBehavior = mainScreenState.scrollBehavior,

                currentSearchQuery = currentSearchQuery,
                onSearchQueryChange = { newQuery -> currentSearchQuery = newQuery },
                navController = mainScreenState.navController,
                currentRoute = currentRoute)
        },
        snackbarHost = {
            DefaultSnackbarHost(snackbarState = mainScreenState.snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButtonsColumn(
                mainScreenState = mainScreenState,
                homeViewModel = homeViewModel
            )
        }) { paddingValues: PaddingValues ->
        AppNavigationHost(
            navController = mainScreenState.navController,
            snackBarHostState = mainScreenState.snackbarHostState,
            onFabVisibilityChanged = { mainScreenState.isFabVisible.value = it },
            paddingValues = paddingValues,
            homeViewModel = homeViewModel,
            homeScreenState = homeScreenState,
            currentSearchQuery = currentSearchQuery,
            onSearchQueryChange = { newQuery -> currentSearchQuery = newQuery })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldTabletContent(
    mainScreenState: MainScreenState,
    homeViewModel: HomeViewModel,
    homeScreenState: UiStateScreen<UiHomeData>
) {
    var isRailExpanded: Boolean by remember { mutableStateOf(value = false) }
    val context: Context = LocalContext.current
    val navBackStackEntry: NavBackStackEntry? by mainScreenState.navController.currentBackStackEntryAsState()
    val currentRoute: String? = navBackStackEntry?.destination?.route
    val isSearchScreen: Boolean =
        currentRoute?.startsWith(NavigationRoutes.ROUTE_SEARCH.substringBefore(delimiter = "/{")) == true
    var currentSearchQuery by rememberSaveable { mutableStateOf("") }
    val changelogUrl: String = koinInject(qualifier = named("github_changelog"))
    val buildInfoProvider: BuildInfoProvider = koinInject()
    var showChangelog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .nestedScroll(connection = mainScreenState.scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopAppBar(
                navigationIcon = if (isSearchScreen) Icons.AutoMirrored.Filled.ArrowBack else if (isRailExpanded) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu,
                onNavigationIconClick = {
                    if (isSearchScreen) {
                        mainScreenState.navController.popBackStack()
                    } else {
                        mainScreenState.coroutineScope.launch { isRailExpanded = !isRailExpanded }
                    }
                },
                scrollBehavior = mainScreenState.scrollBehavior,
                currentSearchQuery = currentSearchQuery,
                onSearchQueryChange = { newQuery -> currentSearchQuery = newQuery },
                navController = mainScreenState.navController,
                currentRoute = currentRoute
            )
        },
        floatingActionButton = {
            FloatingActionButtonsColumn(
                mainScreenState = mainScreenState,
                homeViewModel = homeViewModel
            )
        },
        snackbarHost = {
            DefaultSnackbarHost(snackbarState = mainScreenState.snackbarHostState)
        }) { paddingValues: PaddingValues ->
        LeftNavigationRail(
            drawerItems = mainScreenState.uiState.navigationDrawerItems,
            currentRoute = currentRoute,
            isRailExpanded = isRailExpanded,
            paddingValues = paddingValues,
            centerContent = 0.8f,
            onDrawerItemClick = { item: NavigationDrawerItem ->
                handleNavigationItemClick(
                    context = context,
                    item = item,
                    onChangelogRequested = { showChangelog = true },
                )
            },
            content = {
                AppNavigationHost(
                    navController = mainScreenState.navController,
                    snackBarHostState = mainScreenState.snackbarHostState,
                    onFabVisibilityChanged = { mainScreenState.isFabVisible.value = it },
                    paddingValues = PaddingValues(),
                    homeViewModel = homeViewModel,
                    homeScreenState = homeScreenState,
                    currentSearchQuery = currentSearchQuery,
                    onSearchQueryChange = { newQuery -> currentSearchQuery = newQuery }
                )
            }
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