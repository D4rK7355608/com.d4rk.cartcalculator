package com.d4rk.cartcalculator.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.db.table.ShoppingCartTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home Activity, responsible for managing the list of available shopping carts.
 * Handles data loading, deletion, and addition operations.
 */
class HomeViewModel : ViewModel() {
    val openDialog = mutableStateOf(false)
    val carts = mutableStateListOf<ShoppingCartTable>()
    val isLoading = mutableStateOf(true)

    init {
        viewModelScope.launch {
            val loadedCarts = AppCoreManager.database.newCartDao().getAll()
            carts.addAll(loadedCarts)
            isLoading.value = false
        }
    }

    /**
     * Deletes a shopping cart from the list and persists the deletion to the database.
     *
     * @param cartToDelete The [ShoppingCartTable] object representing the cart to be deleted.
     * @param onDeleteFinished A callback function to be called after the deletion operation is completed.
     *                          It takes a boolean parameter indicating whether the deletion was successful.
     */
    fun deleteCart(cartToDelete: ShoppingCartTable, onDeleteFinished: (Boolean) -> Unit) {
        carts.remove(cartToDelete)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AppCoreManager.database.newCartDao().delete(cartToDelete)
                AppCoreManager.database.shoppingCartItemsDao()
                    .deleteItemsFromCart(cartToDelete.cartId)
                onDeleteFinished(true)
            } catch (e: Exception) {
                onDeleteFinished(false)
            }
        }
    }

    /**
     * Adds a new shopping cart to the list and potentially closes any open cart-related dialogs.
     *
     * @param cart The [ShoppingCartTable] object representing the new cart to be added.
     */
    fun addCart(cart: ShoppingCartTable) {
        carts.add(cart)
        openDialog.value = false
    }
}