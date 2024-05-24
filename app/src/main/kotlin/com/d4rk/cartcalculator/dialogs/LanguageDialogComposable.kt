package com.d4rk.cartcalculator.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.store.DataStore
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun LanguageDialog(
    dataStore: DataStore,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val selectedLanguage = remember { mutableStateOf("") }
    val languageEntries = stringArrayResource(R.array.preference_language_entries).toList()
    val languageValues = stringArrayResource(R.array.preference_language_values).toList()

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            LanguageDialogContent(
                selectedLanguage,
                dataStore,
                languageEntries,
                languageValues
            )
        },
        icon = {
            Icon(Icons.Outlined.Language, contentDescription = null)
        },
        confirmButton = {
            TextButton(onClick = {
                onLanguageSelected(selectedLanguage.value)
                onDismiss()
            }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}

@Composable
fun LanguageDialogContent(
    selectedLanguage: MutableState<String>,
    dataStore: DataStore,
    languageEntries: List<String>,
    languageValues: List<String>
) {
    LaunchedEffect(Unit) {
        selectedLanguage.value = dataStore.getLanguage().firstOrNull() ?: ""
    }

    Column {
        Text(stringResource(id = R.string.dialog_language_subtitle))

        Column(modifier = Modifier.fillMaxWidth()) {
            for (i in languageEntries.indices) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedLanguage.value = languageValues[i]
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = selectedLanguage.value == languageValues[i],
                        onClick = null
                    )
                    Text(
                        text = languageEntries[i],
                        style = MaterialTheme.typography.bodyMedium.merge()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
        Spacer(modifier = Modifier.height(12.dp))
        Text(stringResource(id = R.string.dialog_info_language))
    }

    LaunchedEffect(selectedLanguage.value) {
        dataStore.saveLanguage(selectedLanguage.value)
    }
}