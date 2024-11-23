package com.d4rk.cartcalculator.ui.screens.main

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.datastore.DataStore
import com.d4rk.cartcalculator.ui.components.AnimatedExtendedFloatingActionButton
import com.d4rk.cartcalculator.ui.components.ads.AdBanner
import com.d4rk.cartcalculator.ui.components.navigation.NavigationDrawer
import com.d4rk.cartcalculator.ui.components.navigation.TopAppBarMain
import com.d4rk.cartcalculator.ui.screens.home.HomeScreen
import com.d4rk.cartcalculator.ui.screens.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun MainScreen() {
    val drawerState : DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context : Context = LocalContext.current
    val view : View = LocalView.current

    NavigationDrawer(
        drawerState = drawerState ,
        view = view ,
        context = context ,
    )
}

@Composable
fun MainScreenContent(
    view : View , drawerState : DrawerState , context : Context , coroutineScope : CoroutineScope
) {
    val viewModel : HomeViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val isFabVisible by viewModel.isFabVisible.collectAsState()

    Scaffold(topBar = {
        TopAppBarMain(
            view = view ,
            drawerState = drawerState ,
            context = context ,
            coroutineScope = coroutineScope
        )
    } , floatingActionButton = {
        AnimatedExtendedFloatingActionButton(visible = isFabVisible , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            viewModel.openNewCartDialog()
        } , text = { Text(text = stringResource(id = R.string.add_new_cart)) } , icon = {
            Icon(
                Icons.Outlined.AddShoppingCart , contentDescription = null
            )
        })
    } , snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    } , bottomBar = {
        val dataStore = DataStore.getInstance(context)
        AdBanner(
            dataStore = dataStore , modifier = Modifier.padding(
                bottom = (WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + 8.dp)
            )
        )
    }) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            HomeScreen(
                context = context ,
                view = view ,
                viewModel = viewModel ,
                snackbarHostState = snackbarHostState
            )
        }
    }
}