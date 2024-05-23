package com.d4rk.cartcalculator.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.d4rk.cartcalculator.data.store.DataStore

/**
 * Factory class for creating instances of [CartViewModel].
 *
 * This factory ensures that the created [CartViewModel] is initialized with the provided
 * cart ID and a DataStore instance. The DataStore instance is used for managing data persistence
 * in the created ViewModel.
 *
 * @property cartId The ID of the cart to be managed by the created ViewModel.
 * @property dataStore The DataStore instance for managing data persistence in the created ViewModel.
 */
class CartViewModelFactory(private val cartId: Int, private val dataStore: DataStore) :
    ViewModelProvider.Factory {

    /**
     * Creates a new instance of the requested ViewModel class.
     *
     * This method checks if the requested ViewModel class is [CartViewModel]. If it is,
     * a new instance of [CartViewModel] is created with the provided cart ID and DataStore instance,
     * and returned. If the requested ViewModel class is not [CartViewModel], an IllegalArgumentException is thrown.
     *
     * @param modelClass The class of the ViewModel to create.
     * @throws IllegalArgumentException if the requested class is not [CartViewModel].
     * @return A newly created ViewModel instance.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(cartId, dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}