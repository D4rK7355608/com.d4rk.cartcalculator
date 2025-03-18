package com.d4rk.cartcalculator.app.main.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.lifecycle.ViewModel
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.cartcalculator.app.main.domain.model.UiMainScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _screenState : MutableStateFlow<UiStateScreen<UiMainScreen>> = MutableStateFlow(value = UiStateScreen(data = UiMainScreen()))
    val screenState : StateFlow<UiStateScreen<UiMainScreen>> = _screenState.asStateFlow()

    init {
        loadNavigationItems()
    }

    private fun loadNavigationItems() {
        _screenState.updateData(newDataState = ScreenState.Success()) { currentData ->
            currentData.copy(
                navigationDrawerItems = listOf(
                    NavigationDrawerItem(
                        title = R.string.settings ,
                        selectedIcon = Icons.Outlined.Settings ,
                    ) , NavigationDrawerItem(
                        title = R.string.help_and_feedback ,
                        selectedIcon = Icons.AutoMirrored.Outlined.HelpOutline ,
                    ) , NavigationDrawerItem(
                        title = R.string.updates ,
                        selectedIcon = Icons.AutoMirrored.Outlined.EventNote ,
                    ) , NavigationDrawerItem(
                        title = R.string.share ,
                        selectedIcon = Icons.Outlined.Share ,
                    )
                )
            )
        }
    }
}