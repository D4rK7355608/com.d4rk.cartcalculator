package com.d4rk.android.apps.weddix.app.events.search.ui

import android.net.Uri
import com.d4rk.android.apps.weddix.app.events.list.domain.model.UiEventWithDetails
import com.d4rk.cartcalculator.app.cart.search.domain.actions.SearchAction
import com.d4rk.cartcalculator.app.cart.search.domain.actions.SearchEvent
import com.d4rk.cartcalculator.app.cart.search.domain.data.model.ui.UiSearchData
import com.d4rk.cartcalculator.app.cart.search.domain.usecases.SearchEventsUseCase
import com.d4rk.android.apps.weddix.core.data.database.table.EventsListTable
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
    private val searchEventsUseCase : SearchEventsUseCase ,
    private val dispatcherProvider : DispatcherProvider ,
) : ScreenViewModel<UiSearchData , SearchEvent , SearchAction>(
    initialState = UiStateScreen(data = UiSearchData())
) {

    private var searchJob : Job? = null

    override fun onEvent(event : SearchEvent) {
        when (event) {
            is SearchEvent.UpdateQuery -> {
                screenState.updateData(newState = screenState.value.screenState) {

                    it.copy(currentQuery = event.query)
                }

            }

            is SearchEvent.SubmitSearch -> {
                screenState.updateData(newState = screenState.value.screenState) {
                    it.copy(currentQuery = event.query)
                }
                triggerSearch(event.query)
            }

            is SearchEvent.ClearSearch -> {
                searchJob?.cancel()
                screenState.updateData(newState = ScreenState.NoData()) {
                    it.copy(
                        currentQuery = "" , uiEventsWithDetails = UiEventWithDetails() , isLoading = false
                    )
                }
            }

            is SearchEvent.ProcessInitialQuery -> {
                if (screenData?.initialQueryProcessed == false) {
                    val decodedQuery = runCatching {
                        event.encodedQuery?.let { Uri.decode(it) } ?: ""
                    }.getOrDefault("")

                    screenState.updateData(newState = screenState.value.screenState) {
                        it.copy(currentQuery = decodedQuery , initialQueryProcessed = true)
                    }

                    if (decodedQuery.isNotEmpty()) {
                        triggerSearch(decodedQuery)
                    }
                    else {
                        screenState.updateState(newValues = ScreenState.NoData())
                    }
                }
            }
        }
    }

    private fun triggerSearch(query : String , debounce : Boolean = false) {
        searchJob?.cancel()

        if (query.isBlank()) {
            screenState.updateData(newState = ScreenState.NoData()) {
                it.copy(
                    uiEventsWithDetails = UiEventWithDetails() , isLoading = false
                )
            }
            return
        }

        searchJob = launch(context = dispatcherProvider.io) {
            if (debounce) delay(500)

            screenState.setLoading()
            screenState.updateData(newState = ScreenState.IsLoading()) {
                it.copy(isLoading = true)
            }

            searchEventsUseCase.invoke(param = query).collect { result : DataState<List<EventsListTable> , RootError> ->

                screenState.applyResult<List<EventsListTable> , UiSearchData , RootError>(
                    result = result , errorMessage = UiTextHelper.DynamicString("Search failed. Please try again.")
                ) { searchResultsList : List<EventsListTable> , currentSearchData : UiSearchData ->

                    if (searchResultsList.isEmpty() && result is DataState.Success) {

                        screenState.updateState(newValues = ScreenState.NoData())
                    }

                    currentSearchData.copy(
                        uiEventsWithDetails = currentSearchData.uiEventsWithDetails.copy(
                            events = searchResultsList.toMutableList()
                        ) , isLoading = false
                    )
                }

                when (result) {
                    is DataState.Success -> {
                        if (result.data.isEmpty()) {
                            screenState.updateState(newValues = ScreenState.NoData())
                            screenState.updateData(newState = ScreenState.NoData()) {
                                it.copy(isLoading = false , uiEventsWithDetails = UiEventWithDetails())
                            }
                        }
                        else {

                            screenState.updateState(newValues = ScreenState.Success())
                            screenState.updateData(newState = ScreenState.Success()) {
                                it.copy(
                                    isLoading = false , uiEventsWithDetails = it.uiEventsWithDetails.copy(
                                        events = result.data.toMutableList()
                                    )
                                )
                            }
                        }
                    }

                    is DataState.Error -> {

                        screenState.updateData(newState = ScreenState.Error()) {
                            it.copy(isLoading = false)
                        }
                    }

                    is DataState.Loading -> {

                        screenState.updateData(newState = ScreenState.IsLoading()) {
                            it.copy(isLoading = true)
                        }
                    }
                }
            }
        }
    }
}