package com.d4rk.cartcalculator.app.home.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.sections.TopListFilters
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.home.domain.actions.HomeAction
import com.d4rk.cartcalculator.app.home.domain.model.SortOption
import com.d4rk.cartcalculator.app.home.ui.HomeViewModel

@Composable
fun HomeScreenSortFilterRow(viewModel : HomeViewModel) {
    val defaultSortString : String = stringResource(id = R.string.default_sort)
    val dateSortString : String = stringResource(id = R.string.date)
    val lastAddedSortString : String = stringResource(id = R.string.last_added)
    val filters : List<String> = listOf(defaultSortString , dateSortString , lastAddedSortString)
    val selectedFilter : MutableState<String> = remember { mutableStateOf(value = filters[0]) }

    TopListFilters(
        filters = filters , selectedFilter = selectedFilter.value , onFilterSelected = { filter : String ->
            selectedFilter.value = filter
            val sortOption : SortOption = when (filter) {
                dateSortString -> SortOption.OLDEST
                lastAddedSortString -> SortOption.NEWEST
                else -> SortOption.DEFAULT
            }
            viewModel.sendEvent(event = HomeAction.SortCarts(sortOption = sortOption))
        })
}