package com.d4rk.cartcalculator.app.main.ui.routes.cart.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.cartcalculator.app.main.ui.routes.cart.domain.actions.CartAction
import com.d4rk.cartcalculator.core.ui.style.AppTheme
import com.google.android.gms.ads.MobileAds
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartActivity : AppCompatActivity() {

    private var cartId : Int = 0
    private val viewModel : CartViewModel by viewModel()

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this@CartActivity)
        cartId = intent.getIntExtra("cartId" , 0)
        viewModel.sendEvent(event = CartAction.LoadCart(cartId = cartId))

        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background) {
                    CartScreen(activity = this@CartActivity , viewModel = viewModel)
                }
            }
        }
    }
}