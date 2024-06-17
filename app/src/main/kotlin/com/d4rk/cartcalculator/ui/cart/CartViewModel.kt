package com.d4rk.cartcalculator.ui.cart

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cartcalculator.data.core.AppCoreManager
import com.d4rk.cartcalculator.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.data.datastore.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * This ViewModel is responsible for managing the state and interactions of the shopping cart within the CartActivity.
 * It handles the loading, addition, deletion, and updating of cart items in the database.
 * It also manages the selected currency and total price of the cart.
 *
 * @property cartId The unique identifier for the cart being managed.
 * @property selectedCurrency A mutable state holding the selected currency.
 * @property totalPrice A mutable state holding the total price of the cart.
 * @property cartItems A mutable list of cart items, represented by [ShoppingCartItemsTable] objects.
 * @property cart A mutable state holding the [ShoppingCartTable] object of the current cart, if any.
 * @property isLoading A mutable state indicating whether the cart data is being loaded.
 * @property openDialog A mutable state that determines if a dialog related to cart operations is displayed.
 * @property itemQuantities A mutable map that holds the quantity state of each cart item.
 */
class CartViewModel(private val cartId: Int, private val dataStore: DataStore) : ViewModel() {

    val selectedCurrency = mutableStateOf("")
    val totalPrice = mutableDoubleStateOf(0.0)
    val cartItems = mutableStateListOf<ShoppingCartItemsTable>()
    val cart = mutableStateOf<ShoppingCartTable?>(null)
    val isLoading = mutableStateOf(true)
    var openDialog = mutableStateOf(false)
    var openDeleteDialog = mutableStateOf(false)
    var currentCartItemForDeletion: ShoppingCartItemsTable? = null
    private val itemQuantities = mutableMapOf<Int, MutableState<Int>>()
    private val mutex = Mutex()

    init {
        loadCartItems()
        loadSelectedCurrency()
    }

    /**
     * Loads the cart items from the database and updates the UI state.
     *
     * This function is called during the initialization of the ViewModel. It launches a coroutine in the ViewModel's scope.
     * In the coroutine, it performs the following operations:
     * 1. Fetches the cart items associated with the current `cartId` from the database and adds them to the `cartItems` list.
     * 2. Fetches the cart associated with the current `cartId` from the database and sets it as the value of the `cart` state.
     * 3. Sets the `isLoading` state to `false` to indicate that the loading operation is complete.
     * 4. Calls the `calculateTotalPrice` function to update the total price of the cart.
     *
     * Note: All database operations are performed asynchronously to avoid blocking the main thread.
     *
     * @throws Exception If there is an error while fetching data from the database.
     */
    private fun loadCartItems() {
        viewModelScope.launch {
            cartItems.addAll(
                AppCoreManager.database.shoppingCartItemsDao().getItemsByCartId(cartId)
            )
            cart.value = AppCoreManager.database.newCartDao().getCartById(cartId)
            isLoading.value = false
            calculateTotalPrice()
        }
    }

    /**
     * Loads the selected currency from the data datastore and updates the UI state.
     *
     * This function is called during the initialization of the ViewModel. It launches a coroutine in the ViewModel's scope.
     * In the coroutine, it performs the following operations:
     * 1. Fetches the selected currency from the data datastore.
     * 2. If a currency is found, it sets the `selectedCurrency` state to the fetched currency.
     * 3. If no currency is found, it sets the `selectedCurrency` state to an empty string.
     *
     * Note: The data datastore operation is performed asynchronously to avoid blocking the main thread.
     *
     * @throws Exception If there is an error while fetching the selected currency from the data datastore.
     */
    private fun loadSelectedCurrency() {
        viewModelScope.launch {
            selectedCurrency.value = dataStore.getCurrency().firstOrNull() ?: ""
        }
    }

    /**
     * Adds a new item to the cart, updates the UI state, and saves the new item to the database.
     *
     * This function performs the following operations:
     * 1. Sets the `cartId` of the new cart item to the current `cartId`.
     * 2. Adds the new cart item to the `cartItems` list.
     * 3. Calls the `calculateTotalPrice` function to update the total price of the cart.
     * 4. Launches a coroutine in the IO dispatcher to insert the new cart item into the database.
     *
     * Note: The database operation is performed asynchronously to avoid blocking the main thread.
     *
     * @param cartItem The new cart item to be added. This should be a [ShoppingCartItemsTable] object.
     * @throws Exception If there is an error while inserting the new cart item into the database.
     */
    fun addCartItem(cartItem: ShoppingCartItemsTable) {
        viewModelScope.launch {
            mutex.withLock {
                cartItem.cartId = this@CartViewModel.cartId
                val newItemId = AppCoreManager.database.shoppingCartItemsDao().insert(cartItem)
                cartItem.itemId = newItemId.toInt()
                cartItems.add(cartItem)
                calculateTotalPrice()
            }
        }
    }

    /**
     * Retrieves or creates a MutableState for the quantity of a specific cart item.
     * If the quantity state for the item already exists in the `itemQuantities` map, it is returned.
     * If it does not exist, a new MutableState is created with the initial value set to the current quantity of the cart item.
     * This new state is then added to the `itemQuantities` map and returned.
     *
     * @param cartItem The cart item for which the quantity state is needed.
     * @return The MutableState representing the quantity of the cart item.
     */
    fun getQuantityStateForItem(cartItem: ShoppingCartItemsTable): MutableState<Int> {
        return itemQuantities.getOrPut(cartItem.itemId) { mutableIntStateOf(cartItem.quantity) }
    }

    /**
     * Increases the quantity of a cart item by one, updates the UI state, and saves the updated quantity to the database.
     *
     * This function performs the following operations:
     * 1. Retrieves the MutableState for the quantity of the cart item using the `getQuantityStateForItem` function.
     * 2. Increases the value of the quantity state by one.
     * 3. Updates the quantity of the cart item in the `cartItems` list.
     * 4. Launches a coroutine in the IO dispatcher to update the quantity of the cart item in the database.
     * 5. Calls the `calculateTotalPrice` function to update the total price of the cart.
     *
     * Note: The database operation is performed asynchronously to avoid blocking the main thread.
     *
     * @param cartItem The cart item whose quantity is to be increased. This should be a [ShoppingCartItemsTable] object.
     * @throws Exception If there is an error while updating the quantity of the cart item in the database.
     */
    fun increaseQuantity(cartItem: ShoppingCartItemsTable) {
        val quantityState = getQuantityStateForItem(cartItem)
        viewModelScope.launch(Dispatchers.IO) {
            val newQuantity = quantityState.value + 1
            quantityState.value = newQuantity
            cartItem.quantity = newQuantity
            AppCoreManager.database.shoppingCartItemsDao().update(cartItem)
            calculateTotalPrice()
        }
    }

    /**
     * Decreases the quantity of a cart item by one, updates the UI state, and saves the updated quantity to the database.
     * If the quantity reaches zero, the item is removed from the cart and the database.
     *
     * This function performs the following operations:
     * 1. Retrieves the MutableState for the quantity of the cart item using the `getQuantityStateForItem` function.
     * 2. Decreases the value of the quantity state by one, but not less than zero.
     * 3. If the new quantity is greater than zero, it updates the quantity of the cart item in the `cartItems` list and in the database.
     * 4. If the new quantity is zero, it removes the cart item from the `cartItems` list and from the database.
     * 5. Calls the `calculateTotalPrice` function to update the total price of the cart.
     *
     * Note: The database operations are performed asynchronously to avoid blocking the main thread.
     *
     * @param cartItem The cart item whose quantity is to be decreased. This should be a [ShoppingCartItemsTable] object.
     * @throws Exception If there is an error while updating or deleting the cart item in the database.
     */
    fun decreaseQuantity(cartItem: ShoppingCartItemsTable) {
        val quantityState = getQuantityStateForItem(cartItem)
        viewModelScope.launch(Dispatchers.IO) {
            val newQuantity = maxOf(quantityState.value - 1, 0)
            if (newQuantity > 0) {
                quantityState.value = newQuantity
                cartItem.quantity = newQuantity
                AppCoreManager.database.shoppingCartItemsDao().update(cartItem)
            } else {
                AppCoreManager.database.shoppingCartItemsDao().delete(cartItem)
                withContext(Dispatchers.Main) {
                    cartItems.remove(cartItem)
                }
            }
            calculateTotalPrice()
        }
    }

    /**
     * Deletes the specified cart item from the cart and database.
     *
     * @param cartItem The cart item to be deleted.
     */
    fun deleteCartItem(cartItem: ShoppingCartItemsTable) {
        viewModelScope.launch(Dispatchers.IO) {
            AppCoreManager.database.shoppingCartItemsDao().delete(cartItem)
            withContext(Dispatchers.Main) {
                cartItems.remove(cartItem)
                calculateTotalPrice()
            }
        }
    }

    /**
     * Calculates the total price of the cart items and updates the UI state.
     *
     * This function is called whenever a cart item is added, removed, or its quantity is changed.
     * It performs the following operations:
     * 1. Iterates over each cart item in the `cartItems` list.
     * 2. For each cart item, it multiplies the item's price by its quantity and adds the result to the total price.
     * 3. Sets the `totalPrice` state to the calculated total price.
     *
     * Note: The item's price is converted to a double before multiplication to ensure accurate calculations.
     */
    private fun calculateTotalPrice() {
        val total = cartItems.sumOf { item -> item.price.toDouble() * item.quantity }
        totalPrice.doubleValue = total
    }

    /**
     * Saves the current state of cart items to the database. This includes any changes made to the quantity of the cart items.
     * This function is called when the user decides to save their changes and exit the cart.
     *
     * This function performs the following operations:
     * 1. Launches a coroutine in the IO dispatcher to perform database operations asynchronously.
     * 2. Iterates over each cart item in the `cartItems` list.
     * 3. For each cart item, it updates the corresponding record in the database with the current state of the cart item.
     *
     * Note: The database operation is performed asynchronously to avoid blocking the main thread.
     *
     * @throws Exception If there is an error while updating the cart items in the database.
     */
    fun saveCartItems() {
        viewModelScope.launch(Dispatchers.IO) {
            cartItems.forEach { cartItem ->
                AppCoreManager.database.shoppingCartItemsDao().update(cartItem)
            }
        }
    }
}