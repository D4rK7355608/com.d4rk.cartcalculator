package com.d4rk.cartcalculator.app.home.domain.usecases

import android.content.Context
import android.content.Intent
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository
import com.d4rk.cartcalculator.app.cart.ui.CartActivity
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable

class OpenCartUseCase(private val context : Context) : Repository<ShoppingCartTable , Unit> {

    override suspend fun invoke(param : ShoppingCartTable) {
        runCatching {
            val intent = Intent(context , CartActivity::class.java).apply {
                putExtra("cartId" , param.cartId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}