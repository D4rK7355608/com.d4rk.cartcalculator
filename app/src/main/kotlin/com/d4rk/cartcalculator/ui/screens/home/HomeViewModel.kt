package com.d4rk.cartcalculator.ui.screens.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.data.model.ui.screens.UiHomeModel
import com.d4rk.cartcalculator.ui.screens.home.repository.HomeRepository
import com.d4rk.cartcalculator.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application : Application) : BaseViewModel(application) {
    private val repository : HomeRepository = HomeRepository(application = application)

    private val _uiState : MutableStateFlow<UiHomeModel> = MutableStateFlow(UiHomeModel())
    val uiState : StateFlow<UiHomeModel> = _uiState

    init {
        loadCarts()
    }

    private fun loadCarts() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            showLoading()
            repository.loadCartsRepository { carts ->
                _uiState.update { currentState ->
                    currentState.copy(carts = carts.toMutableList())
                }
            }
            hideLoading()
            initializeVisibilityStates()
        }
    }

    private fun initializeVisibilityStates() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            delay(timeMillis = 50L)
            _visibilityStates.value = List(size = _uiState.value.carts.size) { false }
            _uiState.value.carts.indices.forEach { index ->
                delay(timeMillis = index * 8L)
                _visibilityStates.value = List(size = _visibilityStates.value.size) { lessonIndex ->
                    lessonIndex == index || _visibilityStates.value[lessonIndex]
                }
            }
            delay(timeMillis = 50L)
            showFab()
        }
    }

    fun openCart(cart : ShoppingCartTable) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.openCartRepository(cart = cart) {}
        }
    }

    fun addCart(cart : ShoppingCartTable) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.addCartRepository(cart = cart) { addedCart ->
                _uiState.update { currentState ->
                    currentState.copy(carts = (currentState.carts + addedCart).toMutableList())
                }
            }
            initializeVisibilityStates()
        }
    }

    fun deleteCart(cartToDelete : ShoppingCartTable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteCartRepository(cart = cartToDelete) {
                _uiState.update { currentState ->
                    val newList : MutableList<ShoppingCartTable> = currentState.carts.toMutableList()
                    newList.remove(element = cartToDelete)
                    return@update currentState.copy(carts = newList)
                }
            }
        }
    }

    fun openDeleteCartDialog(cart : ShoppingCartTable) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(showDeleteCartDialog = true , cartToDelete = cart)
            }
        }
    }

    fun dismissDeleteCartDialog() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(showDeleteCartDialog = false)
            }
        }
    }

    fun openNewCartDialog() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(showCreateCartDialog = true)
            }
        }
    }

    fun dismissNewCartDialog() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(showCreateCartDialog = false)
            }
        }
    }

    fun dismissSnackbar() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(showSnackbar = false)
            }
        }
    }

    fun showSnackbar(message : String) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { currentState ->
                currentState.copy(showSnackbar = true , snackbarMessage = message)
            }
        }
    }
}