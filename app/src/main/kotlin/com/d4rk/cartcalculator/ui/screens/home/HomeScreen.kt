package com.d4rk.cartcalculator.ui.screens.home

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.data.model.ui.screens.UiHomeModel
import com.d4rk.cartcalculator.ui.components.animations.animateVisibility
import com.d4rk.cartcalculator.ui.components.animations.bounceClick
import com.d4rk.cartcalculator.ui.components.animations.hapticSwipeToDismissBox
import com.d4rk.cartcalculator.ui.components.dialogs.AddNewCartAlertDialog
import com.d4rk.cartcalculator.ui.components.dialogs.DeleteCartAlertDialog
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    context : Context ,
    view : View ,
    viewModel : HomeViewModel ,
    snackbarHostState : SnackbarHostState
) {
    val uiState : UiHomeModel by viewModel.uiState.collectAsState()
    val isLoading : Boolean by viewModel.isLoading.collectAsState()
    val visibilityStates by viewModel.visibilityStates.collectAsState()
    val okStringResource = stringResource(id = android.R.string.ok)

    LaunchedEffect(uiState.showSnackbar) {
        if (uiState.showSnackbar) {
            val result = snackbarHostState.showSnackbar(
                message = uiState.snackbarMessage ,
                actionLabel = okStringResource ,
            )
            if (result == SnackbarResult.ActionPerformed || result == SnackbarResult.Dismissed) {
                viewModel.dismissSnackbar()
            }
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
                    .wrapContentSize(Alignment.Center)
        ) {
            Column(
                verticalArrangement = Arrangement.Center ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (isLoading) {
                    CircularProgressIndicator()
                }
                else if (uiState.carts.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.no_carts_available)
                    )
                }
                else {
                    LazyColumn(
                        modifier = Modifier
                                .weight(1f)
                                .padding(bottom = uiState.fabAdHeight)
                    ) {
                        itemsIndexed(
                            items = uiState.carts,
                            key = { _, cart -> cart.cartId }
                        ) { index, cart ->
                            val isVisible = visibilityStates.getOrElse(index) { false }
                            CartItemComposable(
                                cart = cart,
                                onDelete = { viewModel.openDeleteCartDialog(cart) },
                                onCardClick = {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    viewModel.openCart(cart)
                                },
                                uiState = uiState,
                                modifier = Modifier
                                        .animateItem()
                                        .animateVisibility(
                                            visible = isVisible,
                                            index = index
                                        )
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.showCreateCartDialog) {
        AddNewCartAlertDialog(onDismiss = { viewModel.dismissNewCartDialog() } ,
                              onCartCreated = { cart ->
                                  viewModel.addCart(cart)
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
                    deleteCart(it)
                    showSnackbar(context.getString(R.string.snackbar_cart_deleted_success))
                }
            } ,
        )
    }
}

@Composable
fun CartItemComposable(
    cart : ShoppingCartTable ,
    onDelete : (ShoppingCartTable) -> Unit ,
    onCardClick : () -> Unit ,
    uiState : UiHomeModel ,
    modifier : Modifier ,
) {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy" , Locale.getDefault())
    val dateString = dateFormat.format(cart.date)

    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
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

    SwipeToDismissBox(modifier = modifier.hapticSwipeToDismissBox(dismissState) ,
                      state = dismissState ,
                      backgroundContent = {} ,
                      content = {
                          OutlinedCard(shape = RoundedCornerShape(12.dp) ,
                                       modifier = Modifier
                                               .fillMaxWidth()
                                               .padding(top = 8.dp) ,
                                       onClick = {
                                           onCardClick()
                                       }) {
                              Box(modifier = Modifier.clip(MaterialTheme.shapes.medium)) {
                                  Column(
                                      modifier = Modifier
                                              .fillMaxWidth()
                                              .padding(8.dp)
                                  ) {
                                      Row(
                                          horizontalArrangement = Arrangement.SpaceBetween ,
                                          modifier = Modifier.fillMaxWidth()
                                      ) {
                                          Text(
                                              text = cart.name ,
                                              style = MaterialTheme.typography.titleMedium ,
                                              modifier = Modifier.weight(1f)
                                          )
                                          IconButton(
                                              modifier = Modifier.bounceClick() ,
                                              onClick = { onDelete(cart) }) {
                                              Icon(
                                                  imageVector = Icons.Outlined.DeleteForever ,
                                                  contentDescription = "Delete cart" ,
                                                  tint = MaterialTheme.colorScheme.error
                                              )
                                          }
                                      }
                                      Text(
                                          text = stringResource(
                                              R.string.created_on , dateString
                                          ) ,
                                          style = MaterialTheme.typography.bodyMedium ,
                                          textAlign = TextAlign.End
                                      )
                                  }
                              }
                          }
                      })
}