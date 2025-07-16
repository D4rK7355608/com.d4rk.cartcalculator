package com.d4rk.cartcalculator.app.cart.details.ui.utils.helpers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.d4rk.cartcalculator.app.R
import com.d4rk.cartcalculator.app.cart.details.domain.model.ui.CartListItem
import com.d4rk.cartcalculator.app.cart.list.ui.utils.constants.ui.UiConstants
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable

object CartListBuilderHelper {

    @Composable
    fun buildUnifiedCartList(items : List<ShoppingCartItemsTable> , adsEnabled : Boolean) : List<CartListItem> {
        val checked : List<ShoppingCartItemsTable> = items.filter { it.isChecked }
        val unchecked : List<ShoppingCartItemsTable> = items.filter { ! it.isChecked }

        val inCartLabel = stringResource(id = R.string.in_cart)
        val itemsToPickUpLabel = stringResource(id = R.string.items_to_pick_up)

        return buildList {
            if (checked.isNotEmpty()) {
                add(
                    element = CartListItem.Header(
                        label = "$inCartLabel (${checked.size})"
                    )
                )
                addAll(elements = injectAds(items = checked , adsEnabled = adsEnabled))
            }

            if (unchecked.isNotEmpty()) {
                add(
                    element = CartListItem.Header(
                        label = "$itemsToPickUpLabel (${unchecked.size})"
                    )
                )
                addAll(elements = injectAds(items = unchecked , adsEnabled = adsEnabled))
            }
        }
    }

    private fun injectAds(items : List<ShoppingCartItemsTable> , adsEnabled : Boolean) : List<CartListItem> {
        if (! adsEnabled) return items.map { CartListItem.CartItem(item = it) }

        return buildList {
            items.forEachIndexed { index : Int , item : ShoppingCartItemsTable ->
                add(element = CartListItem.CartItem(item = item))
                if ((index + 1) % UiConstants.AD_INTERVAL == 0) add(element = CartListItem.AdItem)
            }
            if (items.isNotEmpty() && items.size % UiConstants.AD_INTERVAL != 0) {
                add(element = CartListItem.AdItem)
            }
        }
    }
}