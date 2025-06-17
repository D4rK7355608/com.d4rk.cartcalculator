package com.d4rk.cartcalculator.app.main.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.fab.AnimatedExtendedFloatingActionButton
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.fab.SmallFloatingActionButton
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.cart.list.domain.actions.HomeEvent
import com.d4rk.cartcalculator.app.cart.list.ui.HomeViewModel
import com.d4rk.cartcalculator.app.main.domain.model.MainScreenState

@Composable
fun FloatingActionButtonsColumn(mainScreenState : MainScreenState , homeViewModel : HomeViewModel) {
    Column(horizontalAlignment = Alignment.End) {
        SmallFloatingActionButton(modifier = Modifier.padding(bottom = SizeConstants.MediumSize) , isVisible = mainScreenState.isFabVisible.value , isExtended = mainScreenState.isFabExtended.value , icon = Icons.Outlined.ImportExport , onClick = {
            homeViewModel.onEvent(HomeEvent.ToggleImportDialog(isOpen = true))
        })

        AnimatedExtendedFloatingActionButton(visible = mainScreenState.isFabVisible.value , onClick = {
            homeViewModel.onEvent(HomeEvent.OpenNewCartDialog)
        } , text = { Text(text = stringResource(id = R.string.add_new_cart)) } , icon = { Icon(imageVector = Icons.Outlined.AddShoppingCart , contentDescription = null) } , modifier = Modifier.bounceClick() , expanded = mainScreenState.isFabExtended.value)
    }
}