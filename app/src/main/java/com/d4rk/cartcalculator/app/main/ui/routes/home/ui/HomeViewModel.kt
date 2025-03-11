package com.d4rk.cartcalculator.app.main.ui.routes.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.setErrors
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.setLoading
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.showSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateState
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenMessageType
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.actions.HomeAction
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.usecases.AddCartUseCase
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.usecases.DeleteCartUseCase
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.usecases.GetCartsUseCase
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.usecases.ImportSharedCartUseCase
import com.d4rk.cartcalculator.app.main.ui.routes.home.domain.usecases.OpenCartUseCase
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.di.DispatcherProvider
import com.d4rk.cartcalculator.core.domain.model.network.DataState
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.domain.usecases.cart.GenerateCartShareLinkUseCase
import com.d4rk.cartcalculator.core.utils.extensions.asUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getCartsUseCase : GetCartsUseCase ,
    private val addCartUseCase : AddCartUseCase ,
    private val deleteCartUseCase : DeleteCartUseCase ,
    private val generateCartShareLinkUseCase : GenerateCartShareLinkUseCase ,
    private val importSharedCartUseCase : ImportSharedCartUseCase ,
    private val openCartUseCase : OpenCartUseCase ,
    private val dispatcherProvider : DispatcherProvider
) : ViewModel() {

    private val _screenState : MutableStateFlow<UiStateScreen<UiHomeData>> = MutableStateFlow(value = UiStateScreen(data = UiHomeData()))
    val screenState : StateFlow<UiStateScreen<UiHomeData>> = _screenState.asStateFlow()

    init {
        loadCarts()
    }

    fun sendEvent(event : HomeAction , isError : Boolean = false) {
        when (event) {
            is HomeAction.LoadCarts -> loadCarts()
            is HomeAction.AddCart -> addCart(cart = event.cart)
            is HomeAction.DeleteCart -> deleteCart(cart = event.cart)
            is HomeAction.GenerateCartShareLink -> generateCartShareLink(event.cart)
            is HomeAction.ImportSharedCart -> importSharedCart(encodedData = event.encodedData)
            is HomeAction.OpenCart -> openCart(cart = event.cart)
            is HomeAction.ToggleImportDialog -> toggleImportDialog(isOpen = event.isOpen)
            is HomeAction.OpenNewCartDialog -> openNewCartDialog()
            is HomeAction.DismissNewCartDialog -> dismissNewCartDialog()
            is HomeAction.OpenDeleteCartDialog -> openDeleteCartDialog(cart = event.cart)
            is HomeAction.DismissDeleteCartDialog -> dismissDeleteCartDialog()
            is HomeAction.ShowSnackbar -> showSnackbar(message = event.message , isError = isError)
            is HomeAction.DismissSnackbar -> dismissSnackbar()
            else -> {}
        }
    }

    private fun loadCarts() {
        viewModelScope.launch {
            getCartsUseCase(param = Unit).flowOn(dispatcherProvider.io).stateIn(viewModelScope , SharingStarted.Lazily , DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        if (result.data.isEmpty()) {
                            _screenState.updateState(ScreenState.NoData())
                        }
                        else {
                            _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                                currentData.copy(
                                    carts = result.data.toMutableList() , showImportDialog = currentData.showImportDialog
                                )
                            }
                        }
                    }

                    is DataState.Error -> {
                        if (result.error == Errors.UseCase.NO_DATA) {
                            println(message = "HomeViewModel::loadCarts => No Data => result = $result")
                            _screenState.updateState(ScreenState.NoData())
                        }
                        else {

                            val uiError = UiSnackbar(type = ScreenMessageType.SNACKBAR , message = result.error.asUiText())
                            _screenState.setErrors(listOf(uiError))
                        }
                    }

                    is DataState.Loading -> _screenState.setLoading()
                    else -> {}
                }
            }
        }
    }

    private fun addCart(cart : ShoppingCartTable) {
        viewModelScope.launch {
            addCartUseCase(param = cart).flowOn(context = dispatcherProvider.io).stateIn(viewModelScope , SharingStarted.Lazily , DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                            currentData.copy(carts = (currentData.carts + result.data).toMutableList())
                        }
                        postSnackbar(message = UiTextHelper.DynamicString("Cart added successfully!") , isError = false)
                    }

                    is DataState.Error -> {
                        if (result.error == Errors.UseCase.NO_DATA) {
                            _screenState.updateState(ScreenState.NoData())
                        }
                        else {
                            postSnackbar(message = result.error.asUiText() , isError = true)
                        }
                    }

                    is DataState.Loading -> _screenState.setLoading()
                    else -> {}
                }
            }
        }
    }

    private fun deleteCart(cart : ShoppingCartTable) {
        viewModelScope.launch {
            deleteCartUseCase(param = cart).flowOn(dispatcherProvider.io).stateIn(viewModelScope , SharingStarted.Lazily , DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _screenState.updateData(ScreenState.Success()) { currentData ->
                            val newList = currentData.carts.toMutableList().apply { remove(cart) }
                            currentData.copy(carts = newList)
                        }
                        if (_screenState.value.data?.carts?.isEmpty() == true) {
                            _screenState.updateState(ScreenState.NoData())
                        }

                        postSnackbar(message = UiTextHelper.DynamicString("Cart deleted successfully!") , isError = false)
                    }

                    is DataState.Error -> {
                        postSnackbar(message = result.error.asUiText() , isError = true)
                    }

                    is DataState.Loading -> _screenState.setLoading()
                    else -> {}
                }
            }
        }
    }

    private fun importSharedCart(encodedData : String) {
        viewModelScope.launch {
            importSharedCartUseCase(param = encodedData).flowOn(dispatcherProvider.io).stateIn(viewModelScope , SharingStarted.Lazily , DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        loadCarts()
                    }

                    is DataState.Error -> {
                        postSnackbar(message = result.error.asUiText() , isError = true)
                    }

                    is DataState.Loading -> _screenState.setLoading()
                    else -> {}
                }
            }
        }
    }

    private fun generateCartShareLink(cart : ShoppingCartTable) {
        viewModelScope.launch {
            generateCartShareLinkUseCase(param = cart.cartId).stateIn(viewModelScope , SharingStarted.Lazily , DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _screenState.updateData(ScreenState.Success()) { currentData ->
                            currentData.copy(shareCartLink = result.data)
                        }
                    }

                    is DataState.Error -> {
                        postSnackbar(message = result.error.asUiText() , isError = true)
                    }

                    is DataState.Loading -> _screenState.setLoading()
                    else -> {}
                }
            }
        }
    }

    private fun openCart(cart : ShoppingCartTable) {
        viewModelScope.launch {
            openCartUseCase(param = cart)
        }
    }

    private fun toggleImportDialog(isOpen : Boolean) {
        _screenState.updateData(newDataState = _screenState.value.screenState) { currentData ->
            currentData.copy(showImportDialog = isOpen)
        }
    }

    private fun openNewCartDialog() {
        _screenState.updateData(newDataState = _screenState.value.screenState) { currentData ->
            currentData.copy(showCreateCartDialog = true)
        }
    }

    private fun dismissNewCartDialog() {
        _screenState.updateData(newDataState = _screenState.value.screenState) { currentData ->
            currentData.copy(showCreateCartDialog = false)
        }
    }

    private fun openDeleteCartDialog(cart : ShoppingCartTable) {
        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
            currentData.copy(showDeleteCartDialog = true , cartToDelete = cart)
        }
    }

    private fun dismissDeleteCartDialog() {
        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
            currentData.copy(showDeleteCartDialog = false)
        }
    }

    private fun showSnackbar(message : String , isError : Boolean) {
        _screenState.showSnackbar(
            UiSnackbar(
                type = ScreenMessageType.SNACKBAR , message = UiTextHelper.DynamicString(message) , isError = isError , timeStamp = System.currentTimeMillis()
            )
        )
    }

    private fun postSnackbar(message : UiTextHelper , isError : Boolean) {
        _screenState.showSnackbar(
            UiSnackbar(
                type = ScreenMessageType.SNACKBAR , message = message , isError = isError , timeStamp = System.currentTimeMillis()
            )
        )
        checkForEmptyCarts()
    }

    private fun dismissSnackbar() {
        _screenState.update { current ->
            current.copy(snackbar = null)
        }
    }

    private fun checkForEmptyCarts() {
        if (_screenState.value.data?.carts.isNullOrEmpty()) {
            _screenState.updateState(ScreenState.NoData())
        }
        else {
            _screenState.updateState(ScreenState.Success())
        }
    }
}