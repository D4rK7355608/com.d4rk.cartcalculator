package com.d4rk.cartcalculator.ui.screens.cart

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.cartcalculator.data.datastore.DataStore
import com.d4rk.cartcalculator.ui.screens.settings.display.theme.style.AppTheme
import com.google.android.gms.ads.MobileAds

class CartActivity : AppCompatActivity() {
    private val viewModel : CartViewModel by viewModels()
    val dataStore = DataStore.getInstance(this)
    private var cartId : Int = 0

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this@CartActivity)
        println("Shopping Cart Calculator -> [CartActivity] onCreate called, Intent flags: ${intent.flags}, launchMode: ${intent.component?.shortClassName}")
        cartId = intent.getIntExtra("cartId" , 0) // Correct cartId should be received here.
        println("Shopping Cart Calculator -> [CartActivity] onCreate called, cartId: $cartId")
        viewModel.loadSelectedCurrency(dataStore)
        viewModel.loadCart(cartId)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background
                ) {
                    CartScreen(activity = this@CartActivity , cartId = cartId)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveCartItems()
    }
}