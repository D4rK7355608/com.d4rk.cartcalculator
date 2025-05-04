package com.d4rk.cartcalculator.app.cart.ui.utils.helpers

import com.d4rk.cartcalculator.app.cart.domain.model.CartListItem
import com.d4rk.cartcalculator.app.home.ui.utils.constants.UiConstants
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable

object CartListBuilderHelper {

    fun buildUnifiedCartList(items : List<ShoppingCartItemsTable> , adsEnabled : Boolean) : List<CartListItem> {
        val checked : List<ShoppingCartItemsTable> = items.filter { it.isChecked }
        val unchecked : List<ShoppingCartItemsTable> = items.filter { ! it.isChecked }

        return buildList {
            if (checked.isNotEmpty()) {
                add(element = CartListItem.Header(label = "In Cart (${checked.size})"))
                addAll(elements = injectAds(items = checked , adsEnabled = adsEnabled))
            }

            if (unchecked.isNotEmpty()) {
                add(element = CartListItem.Header(label = "Items to Pick Up (${unchecked.size})"))
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