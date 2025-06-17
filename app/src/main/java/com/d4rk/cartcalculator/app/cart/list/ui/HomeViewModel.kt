package com.d4rk.cartcalculator.app.cart.list.ui

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
import com.d4rk.cartcalculator.app.cart.list.domain.actions.HomeAction
import com.d4rk.cartcalculator.app.cart.list.domain.actions.HomeEvent
import com.d4rk.cartcalculator.app.cart.list.domain.model.SortOption
import com.d4rk.cartcalculator.app.cart.list.domain.model.ui.UiHomeData
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.AddCartUseCase
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.DeleteCartUseCase
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.GetCartsUseCase
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.ImportSharedCartUseCase
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.OpenCartUseCase
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.UpdateCartNameUseCase
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import com.d4rk.cartcalculator.core.domain.usecases.cart.GenerateCartShareLinkUseCase
import com.d4rk.cartcalculator.core.utils.extensions.asUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val dataStore : DataStore ,
    private val getCartsUseCase : GetCartsUseCase ,
    private val addCartUseCase : AddCartUseCase ,
    private val deleteCartUseCase : DeleteCartUseCase ,
    private val generateCartShareLinkUseCase : GenerateCartShareLinkUseCase ,
    private val importSharedCartUseCase : ImportSharedCartUseCase ,
    private val updateCartNameUseCase : UpdateCartNameUseCase ,
    private val openCartUseCase : OpenCartUseCase ,
    private val dispatcherProvider : DispatcherProvider
) : ScreenViewModel<UiHomeData , HomeEvent , HomeAction>(
    initialState = UiStateScreen(data = UiHomeData())
) {
    private var sortOptionCache: SortOption = SortOption.DEFAULT

    init {
        launch {
            val savedSort = dataStore.sortOption.first()
            sortOptionCache = savedSort
            screenState.updateData(screenState.value.screenState) { current ->
                current.copy(currentSort = savedSort)
            }
            onEvent(HomeEvent.LoadCarts)
        }
    }

    override fun onEvent(event : HomeEvent) {
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
            is HomeEvent.OpenDeleteCartDialog -> updateUi { copy(showDeleteCartDialog = true , cartToDelete = event.cart) }
            is HomeEvent.DismissDeleteCartDialog -> updateUi { copy(showDeleteCartDialog = false) }
            is HomeEvent.RenameCart -> renameCart(event.cart , event.newName)
            is HomeEvent.DismissRenameCartDialog -> updateUi { copy(showRenameCartDialog = false) }
            is HomeEvent.OpenRenameCartDialog -> updateUi { copy(showRenameCartDialog = true , cartToRename = event.cart) }
            is HomeEvent.SortCarts -> sortCarts(event.sortOption)
            is HomeEvent.DismissSnackbar -> screenState.dismissSnackbar()
        }
    }

    private fun loadCarts() {
        launch(dispatcherProvider.io) {
            getCartsUseCase(Unit).flowOn(dispatcherProvider.default).collect { result ->
                screenState.applyResult(result) { carts , current ->
                    current.copy(carts = carts.toMutableList())
                }
                if (result is DataState.Success) {
                    if (result.data.isEmpty()) {
                        screenState.updateState(ScreenState.NoData())
                    } else {
                        sortCarts(sortOptionCache)
                    }
                }
            }
        }
    }

    private fun addCart(cart : ShoppingCartTable) {
        launch(dispatcherProvider.io) {
            addCartUseCase(cart).flowOn(dispatcherProvider.default).collect { result ->
                screenState.applyResult(result) { newCart , current ->
                    current.copy(carts = (current.carts + newCart).toMutableList())
                }

                if (result is DataState.Success) {
                    postSnackbar(UiTextHelper.StringResource(R.string.cart_added_successfully) , false)

                    val shouldOpen = dataStore.openCartsAfterCreation.first()
                    if (shouldOpen) {
                        onEvent(HomeEvent.OpenCart(result.data))
                    }
                    sortCarts(sortOptionCache)
                }
            }
        }
    }

    private fun renameCart(cart : ShoppingCartTable , newName : String) {
        launch(dispatcherProvider.io) {
            updateCartNameUseCase(cart to newName).flowOn(dispatcherProvider.default).collect { result ->
                screenState.applyResult(result) { _ , current ->
                    val updatedList = current.carts.map {
                        if (it.cartId == cart.cartId) it.copy(name = newName) else it
                    }
                    current.copy(carts = updatedList.toMutableList())
                }

                if (result is DataState.Success) {
                    postSnackbar(UiTextHelper.StringResource(R.string.cart_renamed_successfully) , false)
                    sortCarts(sortOptionCache)
                }
            }
        }
    }

    private fun sortCarts(sortOption : SortOption) {
        launch(dispatcherProvider.default) {
            val currentData = screenState.value.data ?: return@launch
            val sorted = when (sortOption) {
                SortOption.ALPHABETICAL -> currentData.carts.sortedBy { it.name.lowercase() }
                SortOption.OLDEST -> currentData.carts.sortedBy { it.date }
                SortOption.NEWEST -> currentData.carts.sortedByDescending { it.date }
                SortOption.DEFAULT -> currentData.carts.sortedBy { it.cartId }
            }

            updateUi {
                copy(carts = sorted.toMutableList() , currentSort = sortOption)
            }
            sortOptionCache = sortOption
            launch(dispatcherProvider.io) { dataStore.saveSortOption(sortOption) }
        }
    }

    private fun deleteCart(cart : ShoppingCartTable) {
        launch(dispatcherProvider.io) {
            deleteCartUseCase(cart).flowOn(dispatcherProvider.default).collect { result ->
                if (result is DataState.Success) {
                    val updatedList = screenData?.carts.orEmpty().toMutableList().apply {
                        remove(cart)
                    }

                    if (updatedList.isEmpty()) {
                        screenState.update {
                            it.copy(
                                screenState = ScreenState.NoData() , data = it.data?.copy(carts = mutableListOf())
                            )
                        }
                    }
                    else {
                        updateUi { copy(carts = updatedList) }
                    }

                    sortCarts(sortOptionCache)

                    postSnackbar(UiTextHelper.StringResource(R.string.cart_deleted_successfully) , false)
                }
            }
        }
    }

    private fun importSharedCart(encodedData : String) {
        launch(dispatcherProvider.io) {
            importSharedCartUseCase(encodedData).flowOn(dispatcherProvider.default).collect { result ->
                when (result) {
                    is DataState.Success -> onEvent(HomeEvent.LoadCarts)
                    is DataState.Error -> postSnackbar(result.error.asUiText() , true)
                    else -> Unit
                }
            }
        }
    }

    private fun generateCartShareLink(cart : ShoppingCartTable) {
        launch(dispatcherProvider.io) {
            generateCartShareLinkUseCase(cart.cartId).flowOn(dispatcherProvider.default).collect { result ->
                when (result) {
                    is DataState.Success -> updateUi { copy(shareLink = result.data) }
                    is DataState.Error -> postSnackbar(result.error.asUiText() , true)
                    else -> Unit
                }
            }
        }
    }

    private fun openCart(cart : ShoppingCartTable) {
        launch(dispatcherProvider.io) {
            openCartUseCase(cart)
        }
    }

    private fun postSnackbar(message : UiTextHelper , isError : Boolean) {
        launch {
            screenState.showSnackbar(UiSnackbar(message = message , isError = isError , timeStamp = System.currentTimeMillis() , type = ScreenMessageType.SNACKBAR))
            checkForEmptyCarts()
        }
    }

    private fun checkForEmptyCarts() {
        val isEmpty = screenData?.carts.isNullOrEmpty()
        screenState.updateState(if (isEmpty) ScreenState.NoData() else ScreenState.Success())
    }

    internal fun updateUi(transform: UiHomeData.() -> UiHomeData) {
        screenState.updateData(screenState.value.screenState) { current ->
            transform(current)
        }
    }
}