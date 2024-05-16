package com.d4rk.cartcalculator.ui.cart

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
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
 * This ViewModel manages the state and interactions of the shopping cart within CartActivity.
 *
 * @property cartId The unique identifier for the cart being managed.
 * @property cartItems A mutable list of cart items, represented by [ShoppingCartItemsTable] objects.
 * @property cart A mutable state holding the [ShoppingCartTable] object of the current cart, if any.
 * @property isLoading A mutable state indicating whether the cart data is being loaded.
 * @property openDialog A mutable state that determines if a dialog related to cart operations is displayed.
 */
class CartViewModel(private val cartId: Int) : ViewModel() {

    val cartItems = mutableStateListOf<ShoppingCartItemsTable>()
    val cart = mutableStateOf<ShoppingCartTable?>(null)
    val isLoading = mutableStateOf(true)
    var openDialog = mutableStateOf(false)
    private val itemQuantities = mutableMapOf<Int, MutableState<Int>>()

    init {
        loadCartItems()
    }

    /**
     * Retrieves or creates a MutableState for the quantity of a specific cart item.
     *
     * @param cartItem The cart item for which the quantity state is needed.
     * @return The MutableState representing the quantity of the cart item.
     */
    fun getQuantityStateForItem(cartItem: ShoppingCartItemsTable): MutableState<Int> {
        return itemQuantities.getOrPut(cartItem.id) { mutableIntStateOf(cartItem.quantity) }
    }

    /**
     * Initiates the loading of cart items from the database and updates the cart state.
     */
    private fun loadCartItems() {
        viewModelScope.launch {
            cartItems.addAll(MyApp.database.shoppingCartItemsDao().getItemsByCartId(cartId))
            cart.value = MyApp.database.newCartDao().getCartById(cartId)
            isLoading.value = false
        }
    }

    /**
     * Adds a new item to the cart and updates the database accordingly.
     *
     * @param cartItem The new cart item to be added.
     */
    fun addCartItem(cartItem: ShoppingCartItemsTable) {
        cartItem.cartId = this.cartId
        cartItems.add(cartItem)
        viewModelScope.launch(Dispatchers.IO) {
            MyApp.database.shoppingCartItemsDao().insert(cartItem)
        }
    }


    /**
     * Increases the quantity of a cart item and updates the database with the new quantity.
     *
     * @param cartItem The cart item whose quantity is to be increased.
     */
    fun increaseQuantity(cartItem: ShoppingCartItemsTable) {
        val quantityState = getQuantityStateForItem(cartItem)
        viewModelScope.launch(Dispatchers.IO) {
            val newQuantity = quantityState.value + 1
            quantityState.value = newQuantity
            cartItem.quantity = newQuantity
            MyApp.database.shoppingCartItemsDao().update(cartItem)
        }
    }

    /**
     * Decreases the quantity of a cart item and updates the database. If the quantity reaches zero, the item is removed from the cart.
     *
     * @param cartItem The cart item whose quantity is to be decreased.
     */
    fun decreaseQuantity(cartItem: ShoppingCartItemsTable) {
        val quantityState = getQuantityStateForItem(cartItem)
        viewModelScope.launch(Dispatchers.IO) {
            val newQuantity = maxOf(quantityState.value - 1, 0)
            if (newQuantity > 0) {
                quantityState.value = newQuantity
                cartItem.quantity = newQuantity
                MyApp.database.shoppingCartItemsDao().update(cartItem)
            } else {
                MyApp.database.shoppingCartItemsDao().delete(cartItem)
                withContext(Dispatchers.Main) {
                    cartItems.remove(cartItem)
                }
            }
        }
    }
}