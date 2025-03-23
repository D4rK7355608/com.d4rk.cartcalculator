package com.d4rk.cartcalculator.app.main.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.d4rk.android.libs.apptoolkit.app.display.theme.style.AppTheme
import com.d4rk.android.libs.apptoolkit.app.startup.ui.StartupActivity
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        lifecycleScope.launch {
            if (DataStore.getInstance(context = this@MainActivity).startup.first()) {
                IntentsHelper.openActivity(context = this@MainActivity , activityClass = StartupActivity::class.java)
                finish()
            }
            else {
                setContent {
                    AppTheme {
                        Surface(modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background) {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}