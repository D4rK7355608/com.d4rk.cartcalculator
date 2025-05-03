package com.d4rk.cartcalculator.app.home.ui

import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.applyResult
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.dismissSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.showSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateState
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenMessageType
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.home.domain.actions.HomeAction
import com.d4rk.cartcalculator.app.home.domain.actions.HomeEvent
import com.d4rk.cartcalculator.app.home.domain.model.SortOption
import com.d4rk.cartcalculator.app.home.domain.model.UiHomeData
import com.d4rk.cartcalculator.app.home.domain.usecases.AddCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.DeleteCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.GetCartsUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.ImportSharedCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.OpenCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.UpdateCartNameUseCase
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import com.d4rk.cartcalculator.core.domain.model.network.Errors
import com.d4rk.cartcalculator.core.domain.usecases.cart.GenerateCartShareLinkUseCase
import com.d4rk.cartcalculator.core.utils.extensions.asUiText
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeViewModel(
    private val dataStore: DataStore,
    private val getCartsUseCase : GetCartsUseCase ,
    private val addCartUseCase : AddCartUseCase ,
    private val deleteCartUseCase : DeleteCartUseCase ,
    private val generateCartShareLinkUseCase : GenerateCartShareLinkUseCase ,
    private val importSharedCartUseCase : ImportSharedCartUseCase ,
    private val updateCartNameUseCase : UpdateCartNameUseCase ,
    private val openCartUseCase : OpenCartUseCase ,
    private val dispatcherProvider : DispatcherProvider
) : ScreenViewModel<UiHomeData , HomeEvent , HomeAction>(initialState = UiStateScreen(data = UiHomeData())) {

    init {
        onEvent(event = HomeEvent.LoadCarts)
    }

    override fun onEvent(event : HomeEvent) {
        when (event) {
            is HomeEvent.LoadCarts -> loadCarts()
            is HomeEvent.AddCart -> addCart(cart = event.cart)
            is HomeEvent.DeleteCart -> deleteCart(cart = event.cart)
            is HomeEvent.GenerateCartShareLink -> generateCartShareLink(cart = event.cart)
            is HomeEvent.ImportSharedCart -> importSharedCart(encodedData = event.encodedData)
            is HomeEvent.OpenCart -> openCart(cart = event.cart)
            is HomeEvent.ToggleImportDialog -> updateUi { copy(showImportDialog = event.isOpen) }
            is HomeEvent.OpenNewCartDialog -> updateUi { copy(showCreateCartDialog = true) }
            is HomeEvent.DismissNewCartDialog -> updateUi { copy(showCreateCartDialog = false) }
            is HomeEvent.OpenDeleteCartDialog -> updateUi { copy(showDeleteCartDialog = true , cartToDelete = event.cart) }
            is HomeEvent.DismissDeleteCartDialog -> updateUi { copy(showDeleteCartDialog = false) }
            is HomeEvent.RenameCart -> renameCart(cart = event.cart , newName = event.newName)
            is HomeEvent.DismissRenameCartDialog -> updateUi { copy(showRenameCartDialog = false) }
            is HomeEvent.OpenRenameCartDialog -> updateUi { copy(showRenameCartDialog = true , cartToRename = event.cart) }
            is HomeEvent.SortCarts -> sortCarts(sortOption = event.sortOption)
            is HomeEvent.DismissSnackbar -> screenState.dismissSnackbar()
        }
    }

    private fun loadCarts() {
        launch(context = dispatcherProvider.io) {

            getCartsUseCase(param = Unit).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
                screenState.applyResult(result) { carts : List<ShoppingCartTable> , current : UiHomeData ->
                    current.copy(carts = carts.toMutableList())
                }
                if (result is DataState.Success && result.data.isEmpty()) {
                    screenState.updateState(newValues = ScreenState.NoData())
                }
            }
        }
    }

    private fun addCart(cart : ShoppingCartTable) {
        launch(context = dispatcherProvider.io) {
            addCartUseCase(param = cart).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
                screenState.applyResult(result = result) { newCart : ShoppingCartTable , current : UiHomeData ->
                    current.copy(carts = (current.carts + newCart).toMutableList())
                }
                if (result is DataState.Success) {
                    postSnackbar(message = UiTextHelper.StringResource(R.string.cart_added_successfully) , isError = false)

                    val shouldOpen : Boolean = dataStore.openCartsAfterCreation.first()
                    if (shouldOpen) {
                        onEvent(event = HomeEvent.OpenCart(cart = result.data))
                    }
                }
            }
        }
    }

    private fun renameCart(cart : ShoppingCartTable , newName : String) {
        launch(context = dispatcherProvider.io) {
            updateCartNameUseCase(param = cart to newName).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result ->
                screenState.applyResult(result = result) { _ , current : UiHomeData ->
                    val updatedList : List<ShoppingCartTable> = current.carts.map {
                        if (it.cartId == cart.cartId) it.copy(name = newName) else it
                    }
                    current.copy(carts = updatedList.toMutableList())
                }
                if (result is DataState.Success) {
                    postSnackbar(UiTextHelper.StringResource(R.string.cart_renamed_successfully) , false)
                }
            }
        }
    }

    private fun sortCarts(sortOption : SortOption) {
        updateUi {
            val sorted : List<ShoppingCartTable> = when (sortOption) {
                SortOption.ALPHABETICAL -> carts.sortedBy { it.name.lowercase() }
                SortOption.OLDEST -> carts.sortedBy { it.date }
                SortOption.NEWEST -> carts.sortedByDescending { it.date }
                SortOption.DEFAULT -> carts.sortedBy { it.cartId }
            }
            copy(carts = sorted.toMutableList() , currentSort = sortOption)
        }
    }

    private fun deleteCart(cart : ShoppingCartTable) {
        launch(context = dispatcherProvider.io) {
            deleteCartUseCase(param = cart).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result : DataState<Unit , Errors> ->
                if (result is DataState.Success) {
                    val updatedList = screenData?.carts?.toMutableList()?.apply { remove(element = cart) } ?: mutableListOf()
                    if (updatedList.isEmpty()) {
                        screenState.update { current : UiStateScreen<UiHomeData> ->
                            current.copy(screenState = ScreenState.NoData() , data = current.data?.copy(carts = mutableListOf()))
                        }
                    }
                    else {
                        updateUi {
                            copy(carts = updatedList)
                        }
                    }

                    postSnackbar(message = UiTextHelper.StringResource(resourceId = R.string.cart_deleted_successfully) , isError = false)
                }
            }
        }
    }

    private fun importSharedCart(encodedData : String) {
        launch(context = dispatcherProvider.io) {
            importSharedCartUseCase(param = encodedData).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result : DataState<Unit , Errors> ->
                if (result is DataState.Success) onEvent(event = HomeEvent.LoadCarts)
                if (result is DataState.Error) postSnackbar(message = result.error.asUiText() , isError = true)
            }
        }
    }

    private fun generateCartShareLink(cart : ShoppingCartTable) {
        launch(context = dispatcherProvider.io) {
            generateCartShareLinkUseCase(param = cart.cartId).stateIn(scope = viewModelScope , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result : DataState<String , Errors> ->
                if (result is DataState.Success) {
                    updateUi { copy(shareLink = result.data) }
                }
                else if (result is DataState.Error) {
                    postSnackbar(message = result.error.asUiText() , isError = true)
                }
            }
        }
    }

    private fun postSnackbar(message : UiTextHelper , isError : Boolean) {
        screenState.showSnackbar(snackbar = UiSnackbar(message = message , isError = isError , timeStamp = System.currentTimeMillis() , type = ScreenMessageType.SNACKBAR))
        checkForEmptyCarts()
    }

    private fun openCart(cart : ShoppingCartTable) {
        launch(context = dispatcherProvider.io) {
            openCartUseCase(param = cart)
        }
    }

    private fun checkForEmptyCarts() {
        val isEmpty : Boolean = screenData?.carts.isNullOrEmpty()
        screenState.updateState(newValues = if (isEmpty) ScreenState.NoData() else ScreenState.Success())
    }

    internal inline fun updateUi(crossinline transform : UiHomeData.() -> UiHomeData) {
        launch {
            screenState.updateData(newState = screenState.value.screenState) { current : UiHomeData ->
                transform(current)
            }
        }
    }
}