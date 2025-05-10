package com.d4rk.cartcalculator.app.cart.details.ui

import android.app.Activity
import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.fab.AnimatedFloatingActionButton
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHost
import com.d4rk.cartcalculator.app.cart.details.domain.actions.CartEvent
import com.d4rk.cartcalculator.app.cart.details.domain.model.ui.UiCartScreen
import com.d4rk.cartcalculator.app.cart.details.ui.components.CartItemsList
import com.d4rk.cartcalculator.app.cart.details.ui.components.CartTotalCard
import com.d4rk.cartcalculator.app.cart.details.ui.components.EmptyCartScreen
import com.d4rk.cartcalculator.app.cart.details.ui.components.effects.CartScreenDialogs
import com.d4rk.cartcalculator.app.cart.details.ui.components.navigation.CartScreenTopAppBar
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(activity : Activity , viewModel : CartViewModel) {
    val scrollBehavior : TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackBarHostState : SnackbarHostState = remember { SnackbarHostState() }
    val context : Context = LocalContext.current
    val screenState : UiStateScreen<UiCartScreen> by viewModel.uiState.collectAsState()

    val uiState : UiCartScreen = screenState.data ?: UiCartScreen()

    val totalPrice : Double = uiState.totalPrice
    val cartItems : List<ShoppingCartItemsTable> = uiState.cartItems

    Scaffold(modifier = Modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection) , topBar = {
        CartScreenTopAppBar(screenState = screenState , viewModel = viewModel , activity = activity , context = context , scrollBehavior = scrollBehavior)
    } , bottomBar = {
        AnimatedVisibility(visible = totalPrice > 0 && cartItems.isNotEmpty() , enter = fadeIn() + expandVertically() , exit = fadeOut() + shrinkVertically()) {
            CartTotalCard(uiState = uiState)
        }
    } , snackbarHost = {
        DefaultSnackbarHost(snackbarState = snackBarHostState)
    } , floatingActionButton = {
        AnimatedFloatingActionButton(isVisible = viewModel.areAllItemsChecked() , icon = Icons.Outlined.DeleteForever , onClick = { viewModel.onEvent(event = CartEvent.OpenClearAllDialog(isOpen = true)) })
    }) { paddingValues : PaddingValues ->
        CartScreenStates(paddingValues = paddingValues , screenState = screenState , viewModel = viewModel , snackbarHostState = snackBarHostState)
    }
}

@Composable
fun CartScreenStates(paddingValues : PaddingValues , screenState : UiStateScreen<UiCartScreen> , viewModel : CartViewModel , snackbarHostState : SnackbarHostState) {
    ScreenStateHandler(screenState = screenState , onLoading = {
        LoadingScreen()
    } , onEmpty = {
        EmptyCartScreen(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
        )
    } , onSuccess = {
        CartScreenContent(viewModel = viewModel , paddingValues = paddingValues)
    })
    DefaultSnackbarHandler(screenState = screenState , snackbarHostState = snackbarHostState , getDismissEvent = { CartEvent.DismissSnackbar } , onEvent = { viewModel.onEvent(event = it) })
    CartScreenDialogs(screenState = screenState , viewModel = viewModel)
}

@Composable
fun CartScreenContent(viewModel : CartViewModel , paddingValues : PaddingValues) {
    val layoutDirection : LayoutDirection = LocalLayoutDirection.current

    Column(modifier = Modifier.fillMaxSize()) {
        CartItemsList(
            modifier = Modifier
                    .padding(start = paddingValues.calculateStartPadding(layoutDirection = layoutDirection) , top = paddingValues.calculateTopPadding() , end = paddingValues.calculateEndPadding(layoutDirection = layoutDirection) , bottom = 0.dp)
                    .weight(weight = 1f) , viewModel = viewModel
        )
    }
}