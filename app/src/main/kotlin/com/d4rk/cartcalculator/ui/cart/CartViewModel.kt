package com.d4rk.cartcalculator.ui.cart

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cartcalculator.MyApp
import com.d4rk.cartcalculator.data.db.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.db.table.ShoppingCartTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for the CartActivity. Manages the cart identified by [cartId].
 *
 * @property cartItems LiveData list of [ShoppingCartItemsTable] representing items in the cart.
 * @property cart LiveData containing the [ShoppingCartTable] object for the current cart.
 * @property isLoading LiveData indicating if cart data is currently being loaded.
 * @property openDialog LiveData indicating if a cart-related dialog should be open.
 */
class CartViewModel(private val cartId: Int) : ViewModel() {

    val cartItems = mutableStateListOf<ShoppingCartItemsTable>()
    val cart = mutableStateOf<ShoppingCartTable?>(null)
    val isLoading = mutableStateOf(true)
    var openDialog = mutableStateOf(false)

    init {
        loadCartItems()
    }

    /**
     * Loads the items for the cart from the database.
     */
    private fun loadCartItems() {
        viewModelScope.launch {
            cartItems.addAll(MyApp.database.shoppingCartItemsDao().getItemsByCartId(cartId))
            cart.value = MyApp.database.newCartDao().getCartById(cartId)
            isLoading.value = false
        }
    }

    /**
     * Adds a new item to the cart.
     *
     * @param cartItem The [ShoppingCartItemsTable] object representing the item to add.
     */
    fun addCartItem(cartItem: ShoppingCartItemsTable) {
        cartItems.add(cartItem)
        viewModelScope.launch(Dispatchers.IO) {
            MyApp.database.shoppingCartItemsDao().insert(cartItem)
        }
    }

    /**
     * Increases the quantity of the provided cart item.
     *
     * @param cartItem The [ShoppingCartItemsTable] object representing the item to modify.
     */
    fun increaseQuantity(cartItem: ShoppingCartItemsTable) {
        viewModelScope.launch(Dispatchers.IO) {
            cartItem.quantity++
            MyApp.database.shoppingCartItemsDao().update(cartItem)
            loadCartItems()
        }
    }

    /**
     * Decreases the quantity of the provided cart item. If quantity reaches zero, the item is removed.
     *
     * @param cartItem The [ShoppingCartItemsTable] object representing the item to modify.
     */
    fun decreaseQuantity(cartItem: ShoppingCartItemsTable) {
        viewModelScope.launch(Dispatchers.IO) {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
            } else {
                MyApp.database.shoppingCartItemsDao().delete(cartItem)
                withContext(Dispatchers.Main) {
                    cartItems.remove(cartItem)
                }
            }
            MyApp.database.shoppingCartItemsDao().update(cartItem)
            loadCartItems()
        }
    }
}