package com.d4rk.cartcalculator.ui.cart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.d4rk.cartcalculator.data.store.DataStore
import com.d4rk.cartcalculator.ui.settings.display.theme.style.AppTheme
import com.google.android.gms.ads.MobileAds

class CartActivity : ComponentActivity() {
    private lateinit var viewModel : CartViewModel
    val dataStore = DataStore.getInstance(this)

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this@CartActivity)
        val cartId = intent.getIntExtra("cartId" , 0)
        viewModel = ViewModelProvider(
            this , CartViewModelFactory(cartId , dataStore)
        )[CartViewModel::class.java]
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background
                ) {
                    CartActivityComposable(activity = this@CartActivity , viewModel = viewModel)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveCartItems()
    }
}