package com.d4rk.cartcalculator.app.main.ui.routes.cart.ui

import android.app.Activity
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.ui.components.buttons.AnimatedButtonDirection
import com.d4rk.android.libs.apptoolkit.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.ui.components.snackbar.StatusSnackbarHost
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.ExtraSmallHorizontalSpacer
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.actions.CartAction
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.model.UiCartScreen
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(activity : Activity , viewModel : CartViewModel) {
    val scrollBehavior : TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackBarHostState : SnackbarHostState = remember { SnackbarHostState() }
    val navController : NavHostController = rememberNavController()
    val view : View = LocalView.current
    val screenState : UiStateScreen<UiCartScreen> by viewModel.screenState.collectAsState()

    Scaffold(modifier = Modifier
            .imePadding()
            .nestedScroll(connection = scrollBehavior.nestedScrollConnection) , topBar = {
        LargeTopAppBar(title = {
            Text(text = viewModel.screenState.value.data?.cart?.name ?: stringResource(id = R.string.shopping_cart))
        } , navigationIcon = {
            AnimatedButtonDirection(
                icon = Icons.AutoMirrored.Filled.ArrowBack ,
                contentDescription = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.go_back) ,
                onClick = {
                    activity.finish()
                } ,
            )
        } , actions = {

            AnimatedButtonDirection(icon = Icons.Outlined.AddShoppingCart , contentDescription = "Add Item" , onClick = { viewModel.sendEvent(CartAction.OpenNewCartItemDialog) } , // FIXME: Unresolved reference: OpenNewCartItemDialog
                                    fromRight = true)

            /*        AnimatedButtonDirection(
                        visible = isGooglePayInstalled && cartButtonsVisible , icon = Icons.Outlined.CreditCard , contentDescription = "Open Google Pay" , onClick = { AppUtils.openGooglePayOrWallet(context) } , durationMillis = 400 , fromRight = true
                    )

                    AnimatedButtonDirection(
                        visible = cartButtonsVisible , icon = Icons.Outlined.Share , durationMillis = 500 , contentDescription = "Share Cart" , onClick = { viewModel.shareCart(context , cartId) } , fromRight = true
                    )*/
        } , scrollBehavior = scrollBehavior)
    } ,

             snackbarHost = {
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

    //HomeDialogs(screenState = screenState , viewModel = viewModel)
}

@Composable
fun CartScreenContent(uiState : UiCartScreen , viewModel : CartViewModel , paddingValues : PaddingValues) {
    val (checkedItems , uncheckedItems) = uiState.cartItems.partition { it.isChecked }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .weight(1f)
        ) {
            if (checkedItems.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.in_cart) , style = MaterialTheme.typography.titleMedium , modifier = Modifier
                                .padding(start = SizeConstants.LargeSize , top = SizeConstants.SmallSize)
                                .animateItem()
                    )
                }
                itemsIndexed(items = checkedItems , key = { _ , item -> item.itemId }) { index , cartItem ->
                    CartItemComposable(viewModel = viewModel , cartItem = cartItem , onMinusClick = { item -> viewModel.sendEvent(CartAction.DecreaseQuantity(item)) } , // FIXME: Unresolved reference: DecreaseQuantity
                                       onPlusClick = { item -> viewModel.sendEvent(CartAction.IncreaseQuantity(item)) } , // FIXME: Unresolved reference: IncreaseQuantity
                                       uiState = uiState , modifier = Modifier.animateItem())
                }
            }
            if (uncheckedItems.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.items_to_pick_up) , style = MaterialTheme.typography.titleMedium , modifier = Modifier
                                .padding(start = 16.dp , top = 8.dp)
                                .animateItem()
                    )
                }
                itemsIndexed(items = uncheckedItems , key = { _ , item -> item.itemId }) { index , cartItem ->
                    CartItemComposable(viewModel = viewModel , cartItem = cartItem , onMinusClick = { item -> viewModel.sendEvent(CartAction.DecreaseQuantity(item)) } , // FIXME: Unresolved reference: DecreaseQuantity
                                       onPlusClick = { item -> viewModel.sendEvent(CartAction.IncreaseQuantity(item)) } , // FIXME: Unresolved reference: IncreaseQuantity
                                       uiState = uiState , modifier = Modifier.animateItem())
                }
            }
        }

        Card(
            modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp , topEnd = 16.dp)) , shape = RoundedCornerShape(topStart = 16.dp , topEnd = 16.dp) , colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box(
                modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.total) , style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text(
                            text = String.format(Locale.getDefault() , "%.1f" , uiState.totalPrice.toFloat()).removeSuffix(".0") , style = MaterialTheme.typography.headlineSmall , fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(SizeConstants.MediumSize))
                        Text(
                            text = uiState.selectedCurrency , style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartItemComposable(viewModel : CartViewModel , cartItem : ShoppingCartItemsTable , onMinusClick : (ShoppingCartItemsTable) -> Unit , onPlusClick : (ShoppingCartItemsTable) -> Unit , uiState : UiCartScreen , modifier : Modifier) {
    val view : View = LocalView.current
    var checkedState by remember { mutableStateOf(cartItem.isChecked) }
    val quantityState = viewModel.getQuantityStateForItem(cartItem)
    val interactionSource = remember { MutableInteractionSource() }
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = { target ->
        when (target) {
            SwipeToDismissBoxValue.StartToEnd -> {
                viewModel.sendEvent(CartAction.OpenEditDialog(cartItem))
                false
            }

            SwipeToDismissBoxValue.EndToStart -> {
                viewModel.sendEvent(CartAction.OpenDeleteDialog(cartItem))
                false
            }

            else -> true
        }
    })

    LaunchedEffect(dismissState.targetValue , dismissState.currentValue) {
        if (dismissState.currentValue == dismissState.targetValue) {
            dismissState.reset()
        }
        else if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) {
            if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                viewModel.sendEvent(CartAction.OpenEditDialog(cartItem))
            }
            else if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                viewModel.sendEvent(CartAction.OpenDeleteDialog(cartItem))
            }
        }
    }

    SwipeToDismissBox(state = dismissState , backgroundContent = { /* Optional background */ } , content = {
        Box(modifier = modifier
                .fillMaxWidth()
                .padding(24.dp)) {
            Row(modifier = Modifier.fillMaxSize() , horizontalArrangement = Arrangement.SpaceBetween) {
                Row(modifier = Modifier.weight(1f)) {
                    Checkbox(modifier = Modifier
                            .bounceClick()
                            .padding(end = SizeConstants.LargeSize)
                            .wrapContentSize() , checked = checkedState , onCheckedChange = { isChecked ->
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        checkedState = isChecked
                        viewModel.sendEvent(CartAction.ItemCheckedChange(cartItem , isChecked)) // FIXME: Unresolved reference: ItemCheckedChange
                    })
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = cartItem.name , style = MaterialTheme.typography.bodyLarge , modifier = Modifier.basicMarquee())
                        Row {
                            Text(text = String.format(Locale.getDefault() , "%.1f" , cartItem.price.toFloat()).removeSuffix(".0"))
                            ExtraSmallHorizontalSpacer()
                            Text(text = uiState.selectedCurrency , style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                Row(modifier = Modifier.wrapContentSize() , verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                                .bounceClick()
                                .size(40.dp)
                                .clip(CircleShape)
                                .combinedClickable(interactionSource = interactionSource , indication = remember { ripple() } , onClick = {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    onMinusClick(cartItem)
                                } , onLongClick = {
                                    viewModel.sendEvent(CartAction.OpenDeleteDialog(cartItem)) // FIXME: Unresolved reference: OpenDeleteDialog
                                })) {
                        Icon(imageVector = Icons.Outlined.RemoveCircleOutline , contentDescription = stringResource(id = R.string.decrease_quantity) , modifier = Modifier.align(Alignment.Center))
                    }
                    Text(text = quantityState.toString() , style = MaterialTheme.typography.bodyMedium , modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .animateContentSize())
                    IconButton(
                        modifier = Modifier.bounceClick() , onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            onPlusClick(cartItem)
                        }) {
                        Icon(imageVector = Icons.Outlined.AddCircleOutline , contentDescription = stringResource(id = R.string.increase_quantity))
                    }
                }
            }
        }
    })
}