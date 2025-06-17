package com.d4rk.cartcalculator.app.cart.search.ui

import android.content.Intent
import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.cart.details.ui.CartActivity
import com.d4rk.cartcalculator.app.cart.list.ui.components.CartCategoriesRow
import com.d4rk.cartcalculator.app.cart.search.domain.actions.SearchEvent
import com.d4rk.cartcalculator.app.cart.search.domain.data.model.ui.UiSearchData
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    initialQueryEncoded: String?,
    paddingValues: PaddingValues,
    searchViewModel: SearchViewModel = koinViewModel(),
) {
    val screenStateValue by searchViewModel.uiState.collectAsState()
    val uiData = screenStateValue.data ?: UiSearchData()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!uiData.initialQueryProcessed) {
            searchViewModel.onEvent(SearchEvent.ProcessInitialQuery(initialQueryEncoded))
        }
    }

    ScreenStateHandler(
        screenState = screenStateValue,
        onLoading = { LoadingScreen() },
        onEmpty = {
            NoDataScreen(text = R.string.no_carts_available, icon = Icons.Outlined.RemoveShoppingCart)
        },
        onSuccess = { successData ->
            SearchScreenContent(
                paddingValues = paddingValues,
                searchData = successData,
                onItemClick = { cartId ->
                    val intent = Intent(context, CartActivity::class.java).apply {
                        putExtra("cartId", cartId)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            )
        }
    )
}

@Composable
fun SearchScreenContent(
    paddingValues: PaddingValues,
    searchData: UiSearchData,
    onItemClick: (cartId: Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = SizeConstants.MediumSize),
        verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
    ) {
        items(searchData.carts, key = { it.cartId }) { cartItem ->
            SearchCartItem(cart = cartItem, onClick = { onItemClick(cartItem.cartId) })
        }
    }
}

@Composable
fun SearchCartItem(cart: ShoppingCartTable, onClick: () -> Unit) {
    val view = LocalView.current
    val dateString = remember(cart.date) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(cart.date))
    }

    OutlinedCard(
        shape = RoundedCornerShape(SizeConstants.MediumSize),
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            onClick()
        }
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(SizeConstants.MediumSize)) {
            Text(
                text = cart.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            SmallVerticalSpacer()
            Text(
                text = stringResource(R.string.created_on, dateString),
                style = MaterialTheme.typography.labelMedium
            )
            SmallVerticalSpacer()
            CartCategoriesRow(cart = cart)
        }
    }
}
