package com.d4rk.cartcalculator.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.db.table.ShoppingCartTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The `HomeViewModel` class is a central component in the architecture of the `HomeComposable`.
 * It manages the list of available shopping carts and handles data loading, deletion, and addition operations, thereby ensuring a clear separation of concerns.
 *
 * @property openDialog A mutable state that controls the visibility of a dialog.
 * @property carts A mutable state list that holds the shopping cart data.
 * @property isLoading A mutable state that indicates whether data loading is in progress.
 * @property showSnackbar A mutable state that controls the visibility of a snackbar.
 * @property snackbarMessage A mutable state that holds the message to be displayed in the snackbar.
 * @property fabAdHeight A mutable state that holds the height of the Floating Action Button (FAB) ad.
 */
class HomeViewModel : ViewModel() {
    val openDialog = mutableStateOf(false)
    val carts = mutableStateListOf<ShoppingCartTable>()
    val isLoading = mutableStateOf(true)
    val showSnackbar = mutableStateOf(false)
    val snackbarMessage = mutableStateOf("")
    val fabAdHeight = mutableStateOf(0.dp)

    init {
        loadCarts()
    }

    /**
     * The `loadCarts` function is responsible for retrieving shopping cart data from the database.
     * It operates asynchronously, fetching the carts on a background thread to prevent blocking the main thread. Once the carts are fetched, it updates the UI on the main thread.
     *
     * @see viewModelScope
     * @see launch
     * @see Dispatchers.IO
     * @see withContext
     * @see Dispatchers.Main
     * @see carts
     * @see isLoading
     */
    private fun loadCarts() {
        viewModelScope.launch(Dispatchers.IO) {
            val loadedCarts = AppCoreManager.database.newCartDao().getAll()
            withContext(Dispatchers.Main) {
                carts.addAll(loadedCarts)
            }
            isLoading.value = false
        }
    }

    /**
     * The `deleteCart` function removes a shopping cart from the list and persists the deletion to the database.
     * It operates asynchronously, performing the deletion operation on a background thread.
     *
     * @param cartToDelete The [ShoppingCartTable] object representing the cart to be deleted.
     * @param onDeleteFinished A callback function to be called after the deletion operation is completed. It takes a boolean parameter indicating whether the deletion was successful.
     * @see carts
     * @see viewModelScope
     * @see launch
     * @see Dispatchers.IO
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
     * The `addCart` function adds a new shopping cart to the list and potentially closes any open cart-related dialogs.
     *
     * @param cart The [ShoppingCartTable] object representing the new cart to be added.
     * @see carts
     * @see openDialog
     */
    fun addCart(cart: ShoppingCartTable) {
        carts.add(cart)
        openDialog.value = false
    }

    /**
     * The `setFabHeight` function adjusts the height of the Floating Action Button (FAB) ad.
     *
     * @param height The desired height of the FAB ad, specified in Dp.
     * @see fabAdHeight
     */
    fun setFabHeight(height: Dp) {
        fabAdHeight.value = height
    }
}