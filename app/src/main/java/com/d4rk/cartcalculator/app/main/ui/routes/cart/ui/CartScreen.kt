package com.d4rk.cartcalculator.app.main.ui.routes.cart.ui

import android.app.Activity
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.ui.components.buttons.AnimatedButtonDirection
import com.d4rk.android.libs.apptoolkit.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.ui.components.snackbar.StatusSnackbarHost
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.actions.CartAction
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.model.UiCartScreen
import com.d4rk.cartcalculator.app.main.ui.routes.cart.ui.components.CartItemsList
import com.d4rk.cartcalculator.app.main.ui.routes.cart.ui.components.CartTotalCard
import com.d4rk.cartcalculator.app.main.ui.routes.cart.ui.components.dialogs.CartScreenDialogs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(activity : Activity , viewModel : CartViewModel) {
    val scrollBehavior : TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackBarHostState : SnackbarHostState = remember { SnackbarHostState() }
    val navController : NavHostController = rememberNavController()
    val view : View = LocalView.current
    val screenState : UiStateScreen<UiCartScreen> by viewModel.screenState.collectAsState()

    Scaffold(modifier = Modifier
            .imePadding()
            .nestedScroll(connection = scrollBehavior.nestedScrollConnection) , topBar = {
        LargeTopAppBar(
            title = {
            Text(text = viewModel.screenState.value.data?.cart?.name ?: stringResource(id = R.string.shopping_cart))
        } , navigationIcon = {
            AnimatedButtonDirection(
                icon = Icons.AutoMirrored.Filled.ArrowBack ,
                contentDescription = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.go_back) ,
                onClick = { activity.finish() } ,
            )
        } , actions = {
            AnimatedButtonDirection(
                icon = Icons.Outlined.AddShoppingCart , contentDescription = "Add Item" , onClick = {
                    viewModel.sendEvent(event = CartAction.OpenNewCartItemDialog(isOpen = true))
                                                                                                    } , fromRight = true
            )
  /*                      AnimatedButtonDirection(
                      visible = isGooglePayInstalled && cartButtonsVisible , icon = Icons.Outlined.CreditCard , contentDescription = "Open Google Pay" , onClick = {
                          AppUtils.openGooglePayOrWallet(context)
                                                                                                                                                                   } , durationMillis = 400 , fromRight = true
                  )

                  AnimatedButtonDirection(
                      visible = cartButtonsVisible , icon = Icons.Outlined.Share , durationMillis = 500 , contentDescription = "Share Cart" , onClick = { viewModel.shareCart(context , cartId) } , fromRight = true
                  )*/
        } , scrollBehavior = scrollBehavior
        )
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
        NoDataScreen(text = R.string.your_shopping_cart_is_empty , icon = Icons.Outlined.ShoppingCart)
    } , onSuccess = { uiState ->
        CartScreenContent(uiState = uiState , viewModel = viewModel , paddingValues = paddingValues)
    })

    //HomeScreenSnackbar(screenState = screenState , viewModel = viewModel , snackbarHostState = snackbarHostState)

    CartScreenDialogs(screenState = screenState , viewModel = viewModel)
}

@Composable
fun CartScreenContent(uiState : UiCartScreen , viewModel : CartViewModel , paddingValues : PaddingValues) {

    Column(modifier = Modifier.fillMaxSize()) {
        CartItemsList(uiState = uiState , modifier = Modifier
                .padding(paddingValues = paddingValues)
                .weight(weight = 1f) , viewModel = viewModel)
        CartTotalCard(uiState = uiState)
    }
}