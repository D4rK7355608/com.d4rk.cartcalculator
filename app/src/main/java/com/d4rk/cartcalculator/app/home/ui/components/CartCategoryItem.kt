package com.d4rk.cartcalculator.app.home.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.home.domain.model.CartCategory

@Composable
fun CartCategoryItem(cartCategory : CartCategory) {
    Card(shape = MaterialTheme.shapes.small , colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
        Row(modifier = Modifier.padding(all = SizeConstants.ExtraSmallSize) , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.spacedBy(space = SizeConstants.ExtraSmallSize)) {
            Icon(modifier = Modifier.then(other = Modifier.size(size = SizeConstants.ButtonIconSize)) , imageVector = cartCategory.icon , contentDescription = stringResource(id = R.string.category_icon_description , cartCategory.text))
            Text(text = cartCategory.text , style = MaterialTheme.typography.labelSmall)
        }
    }
}