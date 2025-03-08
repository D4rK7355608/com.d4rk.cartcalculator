package com.d4rk.cartcalculator.app.main.ui.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.ui.components.buttons.AnimatedButtonDirection
import com.d4rk.cartcalculator.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(navigationIcon : ImageVector , onNavigationIconClick : () -> Unit , scrollBehavior : TopAppBarScrollBehavior) {
    TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) } , navigationIcon = {
        AnimatedButtonDirection(
            icon = navigationIcon ,
            contentDescription = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.go_back) ,
            onClick = {
                onNavigationIconClick()
            } ,
        )
    } , actions = {
        AnimatedButtonDirection(
            fromRight = true ,
            icon = Icons.Outlined.VolunteerActivism ,
            contentDescription = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.go_back) ,
            onClick = {
                // TODO: Support Activity
            } ,
        )
    } , scrollBehavior = scrollBehavior)
}