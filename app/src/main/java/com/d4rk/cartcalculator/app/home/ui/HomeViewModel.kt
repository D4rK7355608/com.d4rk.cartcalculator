package com.d4rk.cartcalculator.app.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
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
import com.d4rk.cartcalculator.app.home.domain.actions.HomeAction
import com.d4rk.cartcalculator.app.home.domain.model.SortOption
import com.d4rk.cartcalculator.app.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.home.domain.usecases.AddCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.DeleteCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.GetCartsUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.ImportSharedCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.OpenCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.UpdateCartNameUseCase
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
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
    private val updateCartNameUseCase : UpdateCartNameUseCase ,
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
            is HomeAction.RenameCart -> renameCart(cart = event.cart , newName = event.newName)
            is HomeAction.DismissRenameCartDialog -> dismissRenameCartDialog()
            is HomeAction.OpenRenameCartDialog -> openRenameCartDialog(cart = event.cart)
            is HomeAction.SortCarts -> sortCarts(event.sortOption)
        }
    }

    private fun loadCarts() {
        viewModelScope.launch {
            getCartsUseCase(param = Unit).flowOn(context = dispatcherProvider.io).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        if (result.data.isEmpty()) {
                            _screenState.updateState(newValues = ScreenState.NoData())
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
                            _screenState.updateState(newValues = ScreenState.NoData())
                        }
                        else {

                            val uiError = UiSnackbar(type = ScreenMessageType.SNACKBAR , message = result.error.asUiText())
                            _screenState.setErrors(errors = listOf(element = uiError))
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
            addCartUseCase(param = cart).flowOn(context = dispatcherProvider.io).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                            currentData.copy(carts = (currentData.carts + result.data).toMutableList())
                        }
                        postSnackbar(message = UiTextHelper.DynamicString(content = "Cart added successfully!") , isError = false)
                    }

                    is DataState.Error -> {
                        if (result.error == Errors.UseCase.NO_DATA) {
                            _screenState.updateState(newValues = ScreenState.NoData())
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

    private fun openRenameCartDialog(cart : ShoppingCartTable) {
        viewModelScope.launch {
            _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                currentData.copy(showRenameCartDialog = true , cartToRename = cart)
            }
        }
    }

    private fun dismissRenameCartDialog() {
        viewModelScope.launch {
            _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                currentData.copy(showRenameCartDialog = false)
            }
        }
    }

    private fun renameCart(cart : ShoppingCartTable , newName : String) {
        viewModelScope.launch {
            updateCartNameUseCase(param = Pair(cart , newName)).flowOn(context = dispatcherProvider.io).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                            val updatedList = currentData.carts.map {
                                if (it.cartId == cart.cartId) it.copy(name = newName) else it
                            }.toMutableList()
                            currentData.copy(carts = updatedList)
                        }
                        postSnackbar(message = UiTextHelper.DynamicString(content = "Cart renamed successfully!") , isError = false)
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

    private fun sortCarts(sortOption : SortOption) {
        viewModelScope.launch {
            _screenState.updateData(newDataState = _screenState.value.screenState) { currentData ->
                val sortedCarts = when (sortOption) {
                    SortOption.NAME -> currentData.carts.sortedBy { it.name }
                    SortOption.OLDEST -> currentData.carts.sortedBy { it.date }
                    SortOption.NEWEST -> currentData.carts.sortedByDescending { it.date }
                    else -> currentData.carts
                }
                currentData.copy(carts = sortedCarts.toMutableList() , currentSort = sortOption)
            }
        }
    }

    private fun deleteCart(cart : ShoppingCartTable) {
        viewModelScope.launch {
            deleteCartUseCase(param = cart).flowOn(context = dispatcherProvider.io).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                            val newList : MutableList<ShoppingCartTable> = currentData.carts.toMutableList().apply { remove(cart) }
                            currentData.copy(carts = newList)
                        }
                        if (_screenState.value.data?.carts?.isEmpty() == true) {
                            _screenState.updateState(newValues = ScreenState.NoData())
                        }

                        postSnackbar(message = UiTextHelper.DynamicString(content = "Cart deleted successfully!") , isError = false)
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
            importSharedCartUseCase(param = encodedData).flowOn(context = dispatcherProvider.io).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
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
            generateCartShareLinkUseCase(param = cart.cartId).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
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
        viewModelScope.launch {
            _screenState.updateData(newDataState = _screenState.value.screenState) { currentData ->
                currentData.copy(showImportDialog = isOpen)
            }
        }
    }

    private fun openNewCartDialog() {
        viewModelScope.launch {
            _screenState.updateData(newDataState = _screenState.value.screenState) { currentData ->
                currentData.copy(showCreateCartDialog = true)
            }
        }
    }

    private fun dismissNewCartDialog() {
        viewModelScope.launch {
            _screenState.updateData(newDataState = _screenState.value.screenState) { currentData ->
                currentData.copy(showCreateCartDialog = false)
            }
        }
    }

    private fun openDeleteCartDialog(cart : ShoppingCartTable) {
        viewModelScope.launch {
            _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                currentData.copy(showDeleteCartDialog = true , cartToDelete = cart)
            }
        }
    }

    private fun dismissDeleteCartDialog() {
        viewModelScope.launch {
            _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
                currentData.copy(showDeleteCartDialog = false)
            }
        }
    }

    private fun showSnackbar(message : String , isError : Boolean) {
        viewModelScope.launch {
            _screenState.showSnackbar(
                UiSnackbar(type = ScreenMessageType.SNACKBAR , message = UiTextHelper.DynamicString(message) , isError = isError , timeStamp = System.currentTimeMillis())
            )
        }
    }

    private fun postSnackbar(message : UiTextHelper , isError : Boolean) {
        viewModelScope.launch {
            _screenState.showSnackbar(
                UiSnackbar(type = ScreenMessageType.SNACKBAR , message = message , isError = isError , timeStamp = System.currentTimeMillis())
            )
            checkForEmptyCarts()
        }
    }

    private fun dismissSnackbar() {
        viewModelScope.launch {
            _screenState.update { current ->
                current.copy(snackbar = null)
            }
        }
    }

    private fun checkForEmptyCarts() {
        viewModelScope.launch {
            if (_screenState.value.data?.carts.isNullOrEmpty()) {
                _screenState.updateState(newValues = ScreenState.NoData())
            }
            else {
                _screenState.updateState(newValues = ScreenState.Success())
            }
        }
    }
}