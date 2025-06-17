package com.d4rk.cartcalculator.app.cart.search.ui

import android.net.Uri
import com.d4rk.cartcalculator.app.cart.search.domain.actions.SearchAction
import com.d4rk.cartcalculator.app.cart.search.domain.actions.SearchEvent
import com.d4rk.cartcalculator.app.cart.search.domain.data.model.ui.UiSearchData
import com.d4rk.cartcalculator.app.cart.search.domain.usecases.SearchEventsUseCase
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.RootError
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.applyResult
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.setLoading
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateState
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class SearchViewModel(
    private val searchEventsUseCase: SearchEventsUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ScreenViewModel<UiSearchData, SearchEvent, SearchAction>(
    initialState = UiStateScreen(data = UiSearchData())
) {

    private var searchJob: Job? = null

    override fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.UpdateQuery -> {
                screenState.updateData(screenState.value.screenState) {
                    it.copy(currentQuery = event.query)
                }
            }

            is SearchEvent.SubmitSearch -> {
                screenState.updateData(screenState.value.screenState) {
                    it.copy(currentQuery = event.query)
                }
                triggerSearch(event.query)
            }

            is SearchEvent.ClearSearch -> {
                searchJob?.cancel()
                screenState.updateData(ScreenState.NoData()) {
                    it.copy(
                        currentQuery = "",
                        carts = mutableListOf(),
                        isLoading = false
                    )
                }
            }

            is SearchEvent.ProcessInitialQuery -> {
                val decodedQuery = runCatching {
                    event.encodedQuery?.let { Uri.decode(it) } ?: ""
                }.getOrDefault("")

                if (decodedQuery != screenData?.currentQuery || screenData?.initialQueryProcessed == false) {
                    screenState.updateData(screenState.value.screenState) {
                        it.copy(currentQuery = decodedQuery, initialQueryProcessed = true)
                    }

                    if (decodedQuery.isNotEmpty()) {
                        triggerSearch(decodedQuery)
                    } else {
                        screenState.updateState(ScreenState.NoData())
                    }
                }
            }
        }
    }

    private fun triggerSearch(query: String, debounce: Boolean = false) {
        searchJob?.cancel()

        if (query.isBlank()) {
            screenState.updateData(ScreenState.NoData()) {
                it.copy(carts = mutableListOf(), isLoading = false)
            }
            return
        }

        searchJob = launch(dispatcherProvider.io) {
            if (debounce) delay(500)

            screenState.setLoading()
            screenState.updateData(ScreenState.IsLoading()) { it.copy(isLoading = true) }

            searchEventsUseCase.invoke(query).collect { result: DataState<List<ShoppingCartTable>, RootError> ->

                screenState.applyResult<List<ShoppingCartTable>, UiSearchData, RootError>(
                    result = result,
                    errorMessage = UiTextHelper.DynamicString("Search failed. Please try again.")
                ) { list: List<ShoppingCartTable>, current: UiSearchData ->

                    if (list.isEmpty() && result is DataState.Success) {
                        screenState.updateState(ScreenState.NoData())
                    }

                    current.copy(carts = list.toMutableList(), isLoading = false)
                }

                when (result) {
                    is DataState.Success -> {
                        if (result.data.isEmpty()) {
                            screenState.updateState(ScreenState.NoData())
                            screenState.updateData(ScreenState.NoData()) {
                                it.copy(isLoading = false, carts = mutableListOf())
                            }
                        } else {
                            screenState.updateState(ScreenState.Success())
                            screenState.updateData(ScreenState.Success()) {
                                it.copy(isLoading = false, carts = result.data.toMutableList())
                            }
                        }
                    }

                    is DataState.Error -> {
                        screenState.updateData(ScreenState.Error()) { it.copy(isLoading = false) }
                    }

                    is DataState.Loading -> {
                        screenState.updateData(ScreenState.IsLoading()) { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }
}