package com.d4rk.cartcalculator.app.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.sections.TopListFilters
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.home.domain.actions.HomeEvent
import com.d4rk.cartcalculator.app.home.domain.model.SortOption
import com.d4rk.cartcalculator.app.home.ui.HomeViewModel

@Composable
fun HomeScreenSortFilterRow(viewModel : HomeViewModel) {
    val defaultSortString : String = stringResource(id = R.string.default_sort)
    val dateSortString : String = stringResource(id = R.string.date)
    val lastAddedSortString : String = stringResource(id = R.string.last_added)
    val alphabeticalString : String = stringResource(id = R.string.alphabetical)
    val filters : List<String> = listOf(defaultSortString , alphabeticalString , dateSortString , lastAddedSortString)
    val selectedFilter : MutableState<String> = remember { mutableStateOf(value = filters[0]) }

    TopListFilters(filters = filters , selectedFilter = selectedFilter.value , onFilterSelected = { filter : String ->
        selectedFilter.value = filter
        val sortOption : SortOption = when (filter) {
            alphabeticalString -> SortOption.ALPHABETICAL
            lastAddedSortString -> SortOption.NEWEST
            dateSortString -> SortOption.OLDEST
            else -> SortOption.DEFAULT
        }
        viewModel.onEvent(event = HomeEvent.SortCarts(sortOption = sortOption))
    } , modifier = Modifier.background(color = MaterialTheme.colorScheme.surface))
}