package com.d4rk.cartcalculator.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory class for creating instances of [CartViewModel].
 *
 * This factory ensures that the created [CartViewModel] is initialized with the provided
 * cart ID.
 *
 * @property cartId The ID of the cart to be managed by the created ViewModel.
 */
class CartViewModelFactory(private val cartId: Int) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the requested ViewModel class.
     *
     * @param modelClass The class of the ViewModel to create.
     *
     * @throws IllegalArgumentException if the requested class is not [CartViewModel].
     *
     * @return A newly created ViewModel instance.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(cartId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}