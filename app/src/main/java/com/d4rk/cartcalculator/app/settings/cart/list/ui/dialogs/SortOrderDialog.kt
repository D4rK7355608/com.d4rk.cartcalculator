package com.d4rk.cartcalculator.app.settings.cart.list.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.cart.list.domain.model.SortOption

@Composable
fun SortOrderDialog(
    current: SortOption,
    onDismiss: () -> Unit,
    onConfirm: (SortOption) -> Unit
) {
    var selected by remember { mutableStateOf(current) }

    BasicAlertDialog(
        onDismiss = onDismiss,
        onConfirm = { onConfirm(selected) },
        icon = Icons.AutoMirrored.Outlined.Sort,
        title = stringResource(id = R.string.sort_order_dialog_title),
        confirmButtonText = stringResource(id = R.string.save),
        content = {
            Column {
                SortOptionRow(
                    text = stringResource(id = R.string.default_sort),
                    selected = selected == SortOption.DEFAULT,
                    onClick = { selected = SortOption.DEFAULT }
                )
                SortOptionRow(
                    text = stringResource(id = R.string.alphabetical),
                    selected = selected == SortOption.ALPHABETICAL,
                    onClick = { selected = SortOption.ALPHABETICAL }
                )
                SortOptionRow(
                    text = stringResource(id = R.string.oldest_first),
                    selected = selected == SortOption.OLDEST,
                    onClick = { selected = SortOption.OLDEST }
                )
                SortOptionRow(
                    text = stringResource(id = R.string.last_added),
                    selected = selected == SortOption.NEWEST,
                    onClick = { selected = SortOption.NEWEST }
                )
            }
        }
    )
}

@Composable
private fun SortOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.RadioButton, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(text = text)
    }
}