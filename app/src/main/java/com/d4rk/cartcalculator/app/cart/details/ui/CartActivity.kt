package com.d4rk.cartcalculator.app.cart.details.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.android.libs.apptoolkit.app.theme.style.AppTheme
import com.d4rk.cartcalculator.app.cart.details.domain.actions.CartEvent
import org.koin.androidx.viewmodel.ext.android.getViewModel

class CartActivity : AppCompatActivity() {

    private lateinit var viewModel : CartViewModel
    private var cartId : Int? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initializeDependencies()
        handleStartup()
    }

    private fun initializeDependencies() {
        viewModel = getViewModel()
    }

    private fun handleStartup() {
        cartId = intent?.getIntExtra("cartId" , - 1)?.takeIf { it > 0 }
        cartId?.let { loadedCartId ->
            viewModel.onEvent(event = CartEvent.LoadCart(cartId = loadedCartId))
        }
        setCartActivityContent()
    }

    private fun setCartActivityContent() {
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background) {
                    CartScreen(activity = this@CartActivity , viewModel = viewModel)
                }
            }
        }
    }
}