package com.d4rk.cartcalculator.app.main.domain.model

import com.d4rk.android.libs.apptoolkit.data.model.ui.navigation.NavigationDrawerItem

data class UiMainScreen(
    val showSnackbar : Boolean = false , val snackbarMessage : String = "" , val showDialog : Boolean = false , val navigationDrawerItems : List<NavigationDrawerItem> = listOf()
)