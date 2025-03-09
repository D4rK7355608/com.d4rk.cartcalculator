package com.d4rk.cartcalculator.app.main.ui.routes.home.domain.usecases

import android.content.Context
import android.content.Intent
import com.d4rk.cartcalculator.app.main.ui.routes.cart.ui.CartActivity
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import com.d4rk.cartcalculator.core.domain.usecases.Repository

class OpenCartUseCase(private val context: Context) : Repository<ShoppingCartTable , Unit> {

    override suspend fun invoke(param: ShoppingCartTable) {
        runCatching {
            val intent = Intent(context, CartActivity::class.java).apply {
                putExtra("cartId", param.cartId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}