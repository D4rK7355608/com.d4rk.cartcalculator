package com.d4rk.cartcalculator.app.cart.ui

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.StatusSnackbarHost
import com.d4rk.cartcalculator.app.cart.domain.model.UiCartScreen
import com.d4rk.cartcalculator.app.cart.ui.components.CartItemsList
import com.d4rk.cartcalculator.app.cart.ui.components.CartTotalCard
import com.d4rk.cartcalculator.app.cart.ui.components.EmptyCartScreen
import com.d4rk.cartcalculator.app.cart.ui.components.dialogs.CartScreenDialogs
import com.d4rk.cartcalculator.app.cart.ui.components.navigation.CartScreenTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(activity : Activity , viewModel : CartViewModel) {
    val scrollBehavior : TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackBarHostState : SnackbarHostState = remember { SnackbarHostState() }
    val navController : NavHostController = rememberNavController()
    val view : View = LocalView.current
    val context : Context = LocalContext.current
    val screenState : UiStateScreen<UiCartScreen> by viewModel.screenState.collectAsState()

    Scaffold(modifier = Modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection) , topBar = {
        CartScreenTopAppBar(screenState = screenState , viewModel = viewModel , activity = activity , context = context , scrollBehavior = scrollBehavior)
    } , snackbarHost = {
        StatusSnackbarHost(snackBarHostState = snackBarHostState , view = view , navController = navController)
    }) { paddingValues ->
        CartScreenStates(paddingValues = paddingValues , screenState = screenState , viewModel = viewModel)
    }
}

@Composable
fun CartScreenStates(paddingValues : PaddingValues , screenState : UiStateScreen<UiCartScreen> , viewModel : CartViewModel) {
    ScreenStateHandler(screenState = screenState , onLoading = {
        LoadingScreen()
    } , onEmpty = {
        EmptyCartScreen(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
        )
    } , onSuccess = { uiState ->
        CartScreenContent(uiState = uiState , viewModel = viewModel , paddingValues = paddingValues)
    })

    CartScreenDialogs(screenState = screenState , viewModel = viewModel)
}

@Composable
fun CartScreenContent(uiState : UiCartScreen , viewModel : CartViewModel , paddingValues : PaddingValues) {
    val layoutDirection : LayoutDirection = LocalLayoutDirection.current
    Column(modifier = Modifier.fillMaxSize()) {
        CartItemsList(
            modifier = Modifier
                    .padding(start = paddingValues.calculateStartPadding(layoutDirection) , top = paddingValues.calculateTopPadding() , end = paddingValues.calculateEndPadding(layoutDirection) , bottom = 0.dp)
                    .weight(1f) , viewModel = viewModel
        )
        AnimatedVisibility(
            visible = uiState.totalPrice > 0 , enter = fadeIn() + expandVertically() , exit = fadeOut() + shrinkVertically()
        ) {
            CartTotalCard(uiState = uiState)
        }
    }
}