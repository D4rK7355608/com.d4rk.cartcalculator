package com.d4rk.cartcalculator.app.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.applyResult
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateState
import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.ActionEvent
import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent
import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiState
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
import com.d4rk.cartcalculator.core.domain.usecases.cart.GenerateCartShareLinkUseCase
import com.d4rk.cartcalculator.core.utils.extensions.asUiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class HomeViewModel(
    private val getCartsUseCase: GetCartsUseCase,
    private val addCartUseCase: AddCartUseCase,
    private val deleteCartUseCase: DeleteCartUseCase,
    private val generateCartShareLinkUseCase: GenerateCartShareLinkUseCase,
    private val importSharedCartUseCase: ImportSharedCartUseCase,
    private val updateCartNameUseCase: UpdateCartNameUseCase,
    private val openCartUseCase: OpenCartUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ScreenViewModel<UiHomeData, HomeEvent, HomeAction>(
    initialState = UiStateScreen(data = UiHomeData())
) {

    init {
        onEvent(HomeEvent.LoadCarts)
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadCarts -> loadCarts()
            is HomeEvent.AddCart -> addCart(event.cart)
            is HomeEvent.DeleteCart -> deleteCart(event.cart)
            is HomeEvent.GenerateCartShareLink -> generateCartShareLink(event.cart)
            is HomeEvent.ImportSharedCart -> importSharedCart(event.encodedData)
            is HomeEvent.OpenCart -> openCart(event.cart)
            is HomeEvent.ToggleImportDialog -> updateUi { copy(showImportDialog = event.isOpen) }
            is HomeEvent.OpenNewCartDialog -> updateUi { copy(showCreateCartDialog = true) }
            is HomeEvent.DismissNewCartDialog -> updateUi { copy(showCreateCartDialog = false) }
            is HomeEvent.OpenDeleteCartDialog -> updateUi { copy(showDeleteCartDialog = true, cartToDelete = event.cart) }
            is HomeEvent.DismissDeleteCartDialog -> updateUi { copy(showDeleteCartDialog = false) }
            is HomeEvent.RenameCart -> renameCart(event.cart, event.newName)
            is HomeEvent.DismissRenameCartDialog -> updateUi { copy(showRenameCartDialog = false) }
            is HomeEvent.OpenRenameCartDialog -> updateUi { copy(showRenameCartDialog = true, cartToRename = event.cart) }
            is HomeEvent.SortCarts -> sortCarts(event.sortOption)
        }
    }

    private fun loadCarts() {
        launch(dispatcherProvider.io) {
            getCartsUseCase(Unit).stateIn(viewModelScope, SharingStarted.Lazily, DataState.Loading())
                    .collect { result ->
                        screenState.applyResult(result) { carts, current ->
                            current.copy(carts = carts.toMutableList())
                        }
                        if (result is DataState.Success && result.data.isEmpty()) {
                            screenState.updateState(ScreenState.NoData())
                        }
                    }
        }
    }

    private fun addCart(cart: ShoppingCartTable) {
        launch(dispatcherProvider.io) {
            addCartUseCase(cart).stateIn(viewModelScope, SharingStarted.Lazily, DataState.Loading())
                    .collect { result ->
                        screenState.applyResult(result) { newCart, current ->
                            current.copy(carts = (current.carts + newCart).toMutableList())
                        }
                        if (result is DataState.Success) {
                            postSnackbar(UiTextHelper.StringResource(R.string.cart_added_successfully), false)
                        }
                    }
        }
    }

    private fun renameCart(cart: ShoppingCartTable, newName: String) {
        launch(dispatcherProvider.io) {
            updateCartNameUseCase(cart to newName).stateIn(viewModelScope, SharingStarted.Lazily, DataState.Loading())
                    .collect { result ->
                        screenState.applyResult(result) { _, current ->
                            val updatedList = current.carts.map {
                                if (it.cartId == cart.cartId) it.copy(name = newName) else it
                            }
                            current.copy(carts = updatedList.toMutableList())
                        }
                        if (result is DataState.Success) {
                            postSnackbar(UiTextHelper.StringResource(R.string.cart_renamed_successfully), false)
                        }
                    }
        }
    }

    private fun sortCarts(sortOption: SortOption) {
        updateUi {
            val sorted = when (sortOption) {
                SortOption.ALPHABETICAL -> carts.sortedBy { it.name.lowercase() }
                SortOption.OLDEST -> carts.sortedBy { it.date }
                SortOption.NEWEST -> carts.sortedByDescending { it.date }
                SortOption.DEFAULT -> carts.sortedBy { it.cartId }
            }
            copy(carts = sorted.toMutableList(), currentSort = sortOption)
        }
    }

    private fun deleteCart(cart: ShoppingCartTable) {
        launch(dispatcherProvider.io) {
            deleteCartUseCase(cart).stateIn(viewModelScope, SharingStarted.Lazily, DataState.Loading())
                    .collect { result ->
                        if (result is DataState.Success) {
                            updateUi {
                                val updatedList = carts.toMutableList().apply { remove(cart) }
                                copy(carts = updatedList)
                            }
                            checkForEmptyCarts()
                            postSnackbar(UiTextHelper.StringResource(R.string.cart_deleted_successfully), false)
                        }
                    }
        }
    }

    private fun importSharedCart(encodedData: String) {
        launch(dispatcherProvider.io) {
            importSharedCartUseCase(encodedData).stateIn(viewModelScope, SharingStarted.Lazily, DataState.Loading())
                    .collect { result ->
                        if (result is DataState.Success) onEvent(HomeEvent.LoadCarts)
                        if (result is DataState.Error) postSnackbar(result.error.asUiText(), true)
                    }
        }
    }

    private fun generateCartShareLink(cart: ShoppingCartTable) {
        launch(dispatcherProvider.io) {
            generateCartShareLinkUseCase(cart.cartId).stateIn(viewModelScope, SharingStarted.Lazily, DataState.Loading())
                    .collect { result ->
                        if (result is DataState.Success) sendAction(HomeAction.ShareCart(result.data))
                        if (result is DataState.Error) sendAction(HomeAction.ShowSnackbar(result.error.asUiText(), true))
                    }
        }
    }

    private fun openCart(cart: ShoppingCartTable) {
        launch(dispatcherProvider.io) {
            openCartUseCase(cart)
        }
    }

    private fun checkForEmptyCarts() {
        val isEmpty = screenData?.carts.isNullOrEmpty()
        screenState.updateState(if (isEmpty) ScreenState.NoData() else ScreenState.Success())
    }

    private fun postSnackbar(message: UiTextHelper, isError: Boolean) {
        sendAction(HomeAction.ShowSnackbar(message, isError))
        checkForEmptyCarts()
    }

    private inline fun updateUi(crossinline transform : UiHomeData.() -> UiHomeData) {
        launch {
            screenState.updateData(newState = screenState.value.screenState) { current : UiHomeData ->
                transform(current)
            }
        }
    }
}

abstract class ScreenViewModel<T , E : UiEvent , A : ActionEvent>(initialState : UiStateScreen<T>) : BaseViewModel<UiStateScreen<T> , E , A>(initialState) {
    val screenState : MutableStateFlow<UiStateScreen<T>>
        get() = _uiState

    protected val screenData : T?
        get() = currentState.data
}

abstract class BaseViewModel<S : UiState , E : UiEvent , A : ActionEvent>(initialState : S) : ViewModel() {

    protected val _uiState : MutableStateFlow<S> = MutableStateFlow(value = initialState)
    val uiState : StateFlow<S> = _uiState.asStateFlow()

    private val _actionEvent = Channel<A>()
    val actionEvent = _actionEvent.receiveAsFlow()

    protected val currentState : S
        get() = uiState.value

    protected fun sendAction(action: A) {
        launch { _actionEvent.send(action) }
    }

    protected fun launch(
        context : CoroutineContext = EmptyCoroutineContext , block : suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context = context , block = block)

    abstract fun onEvent(event : E)
}