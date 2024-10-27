package com.d4rk.cartcalculator.ui.screens.home

import android.app.Application
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewModelScope
import com.d4rk.cartcalculator.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.data.model.ui.screens.UiHomeModel
import com.d4rk.cartcalculator.ui.screens.home.repository.HomeRepository
import com.d4rk.cartcalculator.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application : Application) : BaseViewModel(application) {
    private val repository = HomeRepository()

    private val _uiState = MutableStateFlow(UiHomeModel())
    val uiState : StateFlow<UiHomeModel> = _uiState

    init {
        loadCarts()
    }

    private fun loadCarts() {
        viewModelScope.launch(coroutineExceptionHandler) {
            showLoading()
            repository.loadCarts { carts ->
                _uiState.update { it.copy(carts = carts.toMutableList()) }
            }
            hideLoading()
        }
    }

    fun addCart(cart : ShoppingCartTable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.addCart(cart) {
                _uiState.update {
                    val newList = it.carts.toMutableList()
                    newList.add(cart)
                    it.copy(carts = newList)
                }
            }
        }
    }

    fun deleteCart(cartToDelete : ShoppingCartTable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteCart(cartToDelete) {
                _uiState.update {
                    val newList = it.carts.toMutableList()
                    newList.remove(cartToDelete)
                    it.copy(carts = newList)
                }
            }
        }
    }

    fun setFabHeight(height : Dp) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { it.copy(fabAdHeight = height) }
        }
    }

    fun openDeleteCartDialog(cart : ShoppingCartTable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { it.copy(showDeleteCartDialog = true , cartToDelete = cart) }
        }
    }

    fun dismissDeleteCartDialog() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { it.copy(showDeleteCartDialog = false) }
        }
    }

    fun openNewCartDialog() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { it.copy(showCreateCartDialog = true) }
        }
    }

    fun dismissNewCartDialog() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { it.copy(showCreateCartDialog = false) }
        }
    }

    fun dismissSnackbar() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { it.copy(showSnackbar = false) }
        }
    }

    fun showSnackbar(message : String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { it.copy(showSnackbar = true , snackbarMessage = message) }
        }
    }
}