package com.d4rk.cartcalculator.app.main.utils.constants

import android.net.Uri

object NavigationRoutes {
    const val ROUTE_HOME : String = "home"
    const val ROUTE_EVENTS_LIST: String = "events_list"
    private const val ROUTE_SEARCH_BASE = "search_screen"
    const val ARG_INITIAL_QUERY = "initialQuery"

    const val ROUTE_SEARCH = "$ROUTE_SEARCH_BASE?$ARG_INITIAL_QUERY={$ARG_INITIAL_QUERY}"

    fun searchScreenRoute(initialQuery: String = ""): String {
        return if (initialQuery.isNotEmpty()) {
            "$ROUTE_SEARCH_BASE?$ARG_INITIAL_QUERY=${Uri.encode(initialQuery)}"
        } else {

            "$ROUTE_SEARCH_BASE?$ARG_INITIAL_QUERY="
        }
    }
}