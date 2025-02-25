package com.d4rk.cartcalculator.ui.screens.home

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.data.model.ui.error.UiErrorModel
import com.d4rk.android.libs.apptoolkit.ui.components.dialogs.ErrorAlertDialog
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.animateVisibility
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.data.model.ui.screens.UiHomeModel
import com.d4rk.cartcalculator.ui.components.ads.AdBanner
import com.d4rk.cartcalculator.ui.components.dialogs.AddNewCartAlertDialog
import com.d4rk.cartcalculator.ui.components.dialogs.DeleteCartAlertDialog
import com.d4rk.cartcalculator.ui.components.dialogs.ImportCartAlertDialog
import com.d4rk.cartcalculator.ui.components.layouts.NoCartsScreen
import com.d4rk.cartcalculator.ui.components.modifiers.hapticSwipeToDismissBox
import com.google.android.gms.ads.AdSize
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    context : Context , view : View , viewModel : HomeViewModel , snackbarHostState : SnackbarHostState , paddingValues : PaddingValues = PaddingValues()
) {
    val uiState : UiHomeModel by viewModel.uiState.collectAsState()
    val uiErrorModel : UiErrorModel by viewModel.uiErrorModel.collectAsState()
    val isLoading : Boolean by viewModel.isLoading.collectAsState()
    val visibilityStates : List<Boolean> by viewModel.visibilityStates.collectAsState()
    val okStringResource : String = stringResource(id = android.R.string.ok)

    LaunchedEffect(key1 = uiState.showSnackbar) {
        if (uiState.showSnackbar) {
            val result : SnackbarResult = snackbarHostState.showSnackbar(
                message = uiState.snackbarMessage ,
                actionLabel = okStringResource ,
            )
            if (result == SnackbarResult.ActionPerformed || result == SnackbarResult.Dismissed) {
                viewModel.dismissSnackbar()
            }
        }
    }
    if (uiErrorModel.showErrorDialog) {
        ErrorAlertDialog(errorMessage = uiErrorModel.errorMessage) {
            viewModel.dismissErrorDialog()
        }
    }

    Box(
        modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp , end = 24.dp , bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center)
        ) {
            Column(
                verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (isLoading) {
                    CircularProgressIndicator()
                }
                else if (uiState.carts.isEmpty()) {
                    NoCartsScreen()
                }
                else {
                    val carts : List<ShoppingCartTable> = uiState.carts
                    val totalItems = carts.size

                    LazyColumn(
                        contentPadding = paddingValues , modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(items = carts) { index , cart ->
                            CartItem(cart = cart , onDelete = { viewModel.openDeleteCartDialog(cart) } , onCardClick = {
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                viewModel.openCart(cart)
                            } , uiState = uiState , modifier = Modifier
                                    .animateItem()
                                    .animateVisibility(visible = visibilityStates.getOrElse(index) { false } , index = index)
                            )

                            if ((index + 1) % 3 == 0) {
                                AdBanner(
                                    modifier = Modifier
                                            .fillMaxWidth()
                                            .height(AdSize.MEDIUM_RECTANGLE.height.dp) , adSize = AdSize.MEDIUM_RECTANGLE
                                )
                            }
                        }

                        if (totalItems % 3 != 0) {
                            item {
                                AdBanner(
                                    modifier = Modifier.fillMaxWidth() , adSize = AdSize.MEDIUM_RECTANGLE
                                )
                                Spacer(modifier = Modifier.height(72.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showImportDialog) {
        ImportCartAlertDialog(onDismiss = {
            viewModel.toggleImportDialog(isOpen = false)
        } , onImport = { encodedData -> viewModel.importSharedCart(encodedData = encodedData) })
    }

    if (uiState.showCreateCartDialog) {
        AddNewCartAlertDialog(onDismiss = { viewModel.dismissNewCartDialog() } , onCartCreated = { cart ->
            viewModel.addCart(cart = cart)
        })
    }

    if (uiState.showDeleteCartDialog) {
        DeleteCartAlertDialog(
            cart = uiState.cartToDelete ,
            onDismiss = {
                viewModel.dismissDeleteCartDialog()
            } ,
            onDeleteConfirmed = {
                with(viewModel) {
                    deleteCart(cartToDelete = it)
                    showSnackbar(context.getString(R.string.snackbar_cart_deleted_success))
                }
            } ,
        )
    }
}

@Composable
fun CartItem(
    cart : ShoppingCartTable ,
    onDelete : (ShoppingCartTable) -> Unit ,
    onCardClick : () -> Unit ,
    uiState : UiHomeModel ,
    modifier : Modifier ,
) {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy" , Locale.getDefault())
    val dateString : String = dateFormat.format(cart.date)

    val dismissState : SwipeToDismissBoxState = rememberSwipeToDismissBoxState(confirmValueChange = {
        if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
            ! uiState.showDeleteCartDialog
        }
        else {
            true
        }
    })

    LaunchedEffect(key1 = dismissState.targetValue , key2 = dismissState.currentValue) {
        when {
            dismissState.currentValue == dismissState.targetValue -> {
                dismissState.reset()
            }

            dismissState.targetValue != SwipeToDismissBoxValue.Settled -> {
                onDelete(cart)
            }
        }
    }

    SwipeToDismissBox(modifier = modifier.hapticSwipeToDismissBox(swipeToDismissBoxState = dismissState) , state = dismissState , backgroundContent = {} , content = {
        OutlinedCard(shape = RoundedCornerShape(size = 12.dp) , modifier = Modifier.fillMaxWidth() , onClick = {
            onCardClick()
        }) {
            Box(modifier = Modifier.clip(shape = MaterialTheme.shapes.medium)) {
                Column(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween , modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = cart.name , style = MaterialTheme.typography.titleMedium , modifier = Modifier.weight(weight = 1f)
                        )
                        IconButton(modifier = Modifier.bounceClick() , onClick = { onDelete(cart) }) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteForever , contentDescription = "Delete cart" , tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Text(
                        text = stringResource(
                            R.string.created_on , dateString
                        ) , style = MaterialTheme.typography.bodyMedium , textAlign = TextAlign.End
                    )
                }
            }
        }
    })
}