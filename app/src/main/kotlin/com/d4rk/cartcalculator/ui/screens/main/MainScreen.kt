package com.d4rk.cartcalculator.ui.screens.main

import android.content.Context
import android.view.View
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.d4rk.cartcalculator.ui.components.navigation.NavigationDrawer

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