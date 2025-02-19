package com.d4rk.cartcalculator.ui.screens.cart

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d4rk.android.libs.apptoolkit.data.model.ui.error.UiErrorModel
import com.d4rk.android.libs.apptoolkit.ui.components.buttons.AnimatedButtonDirection
import com.d4rk.android.libs.apptoolkit.ui.components.dialogs.ErrorAlertDialog
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.animateVisibility
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.model.ui.screens.UiCartScreen
import com.d4rk.cartcalculator.ui.components.ads.AdBanner
import com.d4rk.cartcalculator.ui.components.dialogs.AddNewCartItemAlertDialog
import com.d4rk.cartcalculator.ui.components.dialogs.DeleteCartItemAlertDialog
import com.d4rk.cartcalculator.ui.components.modifiers.hapticSwipeToDismissBox
import com.d4rk.cartcalculator.utils.external.AppUtils
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(activity : CartActivity , cartId : Int) {

    val viewModel : CartViewModel = viewModel()
    val uiErrorModel : UiErrorModel by viewModel.uiErrorModel.collectAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val primaryColor = MaterialTheme.colorScheme.primary

    val uiState : UiCartScreen by viewModel.uiState.collectAsState()
    val isLoading : Boolean by viewModel.isLoading.collectAsState()
    val visibilityStates by viewModel.visibilityStates.collectAsState()

    val context : Context = LocalContext.current
    val isGooglePayInstalled : Boolean = remember { AppUtils.isGooglePayInstalled(context = context) }
    val cartButtonsVisible : Boolean by remember { derivedStateOf { uiState.cartItems.isNotEmpty() } }

    if (uiErrorModel.showErrorDialog) {
        ErrorAlertDialog(errorMessage = uiErrorModel.errorMessage) {
            viewModel.dismissErrorDialog()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(modifier = Modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection) , topBar = {
            LargeTopAppBar(title = {
                Text(
                    text = uiState.cart?.name ?: stringResource(id = R.string.shopping_cart)
                )
            } , navigationIcon = {
                AnimatedButtonDirection(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.go_back),
                    onClick = { activity.finish() },
                )
            } , actions = {

                AnimatedButtonDirection(
                    icon = Icons.Outlined.AddShoppingCart,
                    contentDescription = "Add Item",
                    onClick = { viewModel.toggleOpenDialog() },
                    fromRight = true
                )

                AnimatedButtonDirection(
                    visible = isGooglePayInstalled && cartButtonsVisible,
                    icon = Icons.Outlined.CreditCard,
                    contentDescription = "Open Google Pay",
                    onClick = { AppUtils.openGooglePayOrWallet(context) },
                    durationMillis = 400,
                    fromRight = true
                )

                AnimatedButtonDirection(
                    visible = cartButtonsVisible,
                    icon = Icons.Outlined.Share,
                    durationMillis = 500,
                    contentDescription = "Share Cart",
                    onClick = { viewModel.shareCart(context, cartId) },
                    fromRight = true
                )
            } , scrollBehavior = scrollBehavior)
        }) { paddingValues ->
            Box(
                modifier = Modifier
                        .padding(paddingValues = paddingValues)
                        .fillMaxSize()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                else if (uiState.cartItems.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.your_shopping_cart_is_empty) , modifier = Modifier.align(alignment = Alignment.Center)
                    )
                    AdBanner(
                        modifier = Modifier.align(alignment = Alignment.BottomCenter)
                    )
                }
                else {

                    val (checkedItems : List<ShoppingCartItemsTable> , uncheckedItems : List<ShoppingCartItemsTable>) = uiState.cartItems.partition { it.isChecked }
                    Box {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                modifier = Modifier.weight(1f)
                            ) {
                                if (checkedItems.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = stringResource(id = R.string.in_cart) , style = MaterialTheme.typography.titleMedium , modifier = Modifier
                                                    .padding(start = 16.dp , top = 8.dp)
                                                    .animateItem()
                                        )
                                    }
                                    itemsIndexed(items = checkedItems , key = { _ , item -> item.itemId }) { index , cartItem ->
                                        val isVisible = visibilityStates.getOrElse(index) { false }
                                        CartItemComposable(viewModel = viewModel , cartItem = cartItem , onMinusClick = {
                                            viewModel.decreaseQuantity(cartItem)
                                        } , onPlusClick = {
                                            viewModel.increaseQuantity(cartItem)
                                        } , uiState = uiState , modifier = Modifier
                                                .animateItem()
                                                .animateVisibility(
                                                    visible = isVisible , index = index
                                                ))
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
                                        val isVisible = visibilityStates.getOrElse(index) { false }
                                        CartItemComposable(viewModel = viewModel , cartItem = cartItem , onMinusClick = {
                                            viewModel.decreaseQuantity(cartItem)
                                        } , onPlusClick = {
                                            viewModel.increaseQuantity(cartItem)
                                        } , uiState = uiState , modifier = Modifier
                                                .animateItem()
                                                .animateVisibility(
                                                    visible = isVisible , index = index
                                                ))
                                    }
                                }
                            }

                            AdBanner(
                                modifier = Modifier.padding(all = 12.dp)
                            )

                            Card(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(144.dp)
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 16.dp , topEnd = 16.dp , bottomEnd = 0.dp , bottomStart = 0.dp
                                            )
                                        ) ,
                                shape = RoundedCornerShape(
                                    topStart = 16.dp , topEnd = 16.dp , bottomEnd = 0.dp , bottomStart = 0.dp
                                ) ,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary ,
                                ) ,
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.total) , style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row {
                                            Text(
                                                text = String.format(
                                                    Locale.US , "%.1f" , uiState.totalPrice.toFloat()
                                                ).removeSuffix(".0") , style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = uiState.selectedCurrency , style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (uiState.openDialog) {
                    AddNewCartItemAlertDialog(cartId = cartId , onDismiss = { viewModel.toggleOpenDialog() } , onCartCreated = { cartItem ->
                        cartItem.cartId = cartId
                        viewModel.addCartItem(cartId , cartItem)
                        viewModel.toggleOpenDialog()
                    })

                }

                if (uiState.openDeleteDialog) {
                    val currentItem = uiState.currentCartItemForDeletion
                    if (currentItem != null) {
                        DeleteCartItemAlertDialog(cartItem = currentItem , onDismiss = {
                            viewModel.toggleDeleteDialog(cartItem = null)
                        } , onDeleteConfirmed = {
                            viewModel.deleteCartItem(currentItem)
                            viewModel.toggleDeleteDialog(cartItem = null)
                        })
                    }
                }

                if (uiState.openEditDialog) {
                    val currentItem = uiState.currentCartItemForEdit
                    if (currentItem != null) {
                        AddNewCartItemAlertDialog(cartId = cartId , existingCartItem = currentItem , onDismiss = { viewModel.toggleEditDialog(cartItem = null) } , onCartCreated = { updatedCartItem ->
                            viewModel.editCartItem(cartItem = updatedCartItem)
                            viewModel.toggleEditDialog(cartItem = null)
                        })
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        if (uiState.cartItems.isEmpty()) {
                            Color.Transparent
                        }
                        else {
                            primaryColor
                        }
                    )
        )
    }
}

/**
 * This Composable function displays a cart item with the ability to modify its quantity.
 * It shows the item's name, price, and a numeric representation of quantity, which can be adjusted via plus and minus buttons.
 *
 * @param cartItem The ShoppingCartItemsTable object representing the cart item.
 * @param onMinusClick Lambda function invoked when the minus button is clicked, decreasing the quantity.
 * @param onPlusClick Lambda function invoked when the plus button is clicked, increasing the quantity.
 * @param quantityState A MutableState that holds and updates the quantity of the cart item.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartItemComposable(
    viewModel : CartViewModel ,
    cartItem : ShoppingCartItemsTable ,
    onMinusClick : (ShoppingCartItemsTable) -> Unit ,
    onPlusClick : (ShoppingCartItemsTable) -> Unit ,
    uiState : UiCartScreen ,
    modifier : Modifier ,
) {
    val view : View = LocalView.current

    var checkedState by remember { mutableStateOf(cartItem.isChecked) }

    val quantityState = viewModel.getQuantityStateForItem(cartItem)
    val interactionSource = remember { MutableInteractionSource() }

    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
        when (it) {
            SwipeToDismissBoxValue.StartToEnd -> {
                viewModel.toggleEditDialog(cartItem = cartItem)
                false
            }

            SwipeToDismissBoxValue.EndToStart -> {
                viewModel.toggleDeleteDialog(cartItem = cartItem)
                false
            }

            else -> true
        }
    })

    LaunchedEffect(key1 = dismissState.targetValue , key2 = dismissState.currentValue) {
        when {
            dismissState.currentValue == dismissState.targetValue -> {
                dismissState.reset()
            }

            dismissState.targetValue != SwipeToDismissBoxValue.Settled -> {
                if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                    viewModel.toggleEditDialog(cartItem = cartItem)
                }
                else if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    viewModel.toggleDeleteDialog(cartItem = cartItem)
                }
            }
        }
    }

    SwipeToDismissBox(modifier = modifier.hapticSwipeToDismissBox(dismissState) , state = dismissState , backgroundContent = {} , content = {
        Box(
            modifier = modifier
                    .fillMaxWidth()
                    .padding(all = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize() ,
                horizontalArrangement = Arrangement.SpaceBetween ,
            ) {

                Row(modifier = Modifier.weight(1f)) {
                    Checkbox(modifier = Modifier
                            .bounceClick()
                            .padding(end = 16.dp)
                            .wrapContentSize() , checked = checkedState , onCheckedChange = { isChecked ->
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        checkedState = isChecked
                        viewModel.onItemCheckedChange(
                            cartItem , isChecked
                        )
                    })
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            modifier = Modifier.basicMarquee() , text = cartItem.name , style = MaterialTheme.typography.bodyLarge
                        )
                        Row {
                            Text(
                                text = String.format(Locale.US , "%.1f" , cartItem.price.toFloat()).removeSuffix(suffix = ".0") ,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = uiState.selectedCurrency , style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.wrapContentSize() , verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier
                            .bounceClick()
                            .size(40.dp)
                            .clip(CircleShape)
                            .combinedClickable(
                                interactionSource = interactionSource ,
                                indication = remember { ripple() } ,
                                onClick = {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    onMinusClick(cartItem)
                                } ,
                                onLongClick = {
                                    viewModel.toggleDeleteDialog(cartItem = cartItem)
                                } ,
                            )) {
                        Icon(
                            imageVector = Icons.Outlined.RemoveCircleOutline , contentDescription = stringResource(id = R.string.decrease_quantity) , modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    Text(
                        text = quantityState.toString() , style = MaterialTheme.typography.bodyMedium , modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateContentSize()
                    )

                    IconButton(modifier = Modifier.bounceClick() , onClick = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        onPlusClick(cartItem)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.AddCircleOutline , contentDescription = stringResource(id = R.string.increase_quantity)
                        )
                    }
                }
            }
        }
    })
}