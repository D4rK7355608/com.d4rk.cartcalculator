package com.d4rk.cartcalculator.app.settings.cart.backup.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.settings.cart.utils.helpers.DatabaseBackupHelper
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CartBackupSettingsScreen(
    paddingValues: PaddingValues,
    databaseInterface: DatabaseInterface = koinInject(),
    dataStore: DataStore = koinInject()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val lastBackupDateFromDataStore by dataStore.lastBackupDateFlow.collectAsState(initial = null)

    val displayLastBackupDate =
        lastBackupDateFromDataStore ?: stringResource(R.string.never_backed_up)

    var isLoading by rememberSaveable { mutableStateOf(false) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { outputFileUri: Uri? ->
        if (outputFileUri != null) {
            isLoading = true
            coroutineScope.launch {
                val backupResult = DatabaseBackupHelper.createBackup(
                    context = context,
                    database = databaseInterface,
                    outputFileUri = outputFileUri
                )
                isLoading = false
                if (backupResult.isSuccess) {
                    val newBackupDateString = SimpleDateFormat(
                        "MMM dd, yyyy HH:mm",
                        Locale.getDefault()
                    ).format(Date())

                    dataStore.saveLastBackupDate(newBackupDateString)

                    Toast.makeText(
                        context,
                        context.getString(
                            R.string.backup_successful_detail,
                            backupResult.getOrNull() ?: context.getString(R.string.unknown_location)
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(
                            R.string.backup_failed,
                            backupResult.exceptionOrNull()?.message
                                ?: context.getString(R.string.unknown_error)
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(context, R.string.backup_cancelled_by_user, Toast.LENGTH_SHORT).show()
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()

    ) { inputFileUri: Uri? ->
        if (inputFileUri != null) {
            isLoading = true
            coroutineScope.launch {
                val restoreResult = DatabaseBackupHelper.restoreBackup(
                    context = context,
                    database = databaseInterface,
                    inputFileUri = inputFileUri
                )
                isLoading = false
                if (restoreResult.isSuccess) {

                    Toast.makeText(
                        context,
                        R.string.restore_successful_detail,
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    Toast.makeText(
                        context,
                        context.getString(
                            R.string.restore_failed,
                            restoreResult.exceptionOrNull()?.message
                                ?: context.getString(R.string.unknown_error)
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(context, R.string.restore_cancelled_by_user, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        BackupActionCard(
            title = stringResource(R.string.backup_now_card_title),
            description = stringResource(R.string.backup_now_card_description),
            buttonText = stringResource(id = R.string.backup_now),
            buttonIcon = Icons.Filled.Backup,
            isLoading = isLoading,
            onButtonClick = {
                if (!isLoading) {
                    val timestamp = SimpleDateFormat(
                        "yyyyMMdd_HHmmss",
                        Locale.getDefault()
                    ).format(Date())
                    val proposedFileName = "cart_backup_$timestamp.json"
                    createDocumentLauncher.launch(proposedFileName)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        BackupActionCard(
            title = stringResource(R.string.restore_card_title),
            description = stringResource(R.string.restore_card_description),
            buttonText = stringResource(id = R.string.restore_from_backup),
            buttonIcon = Icons.Filled.Restore,
            isOutlinedButton = true,
            isLoading = isLoading,
            onButtonClick = {
                if (!isLoading) {

                    openDocumentLauncher.launch(arrayOf("application/json"))
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.backup_information_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        InfoRow(
            icon = Icons.Outlined.Info,
            label = stringResource(R.string.last_backup_date_label),

            value = displayLastBackupDate
        )
    }
}

@Composable
fun BackupActionCard(
    title: String,
    description: String,
    buttonText: String,
    buttonIcon: ImageVector,
    isOutlinedButton: Boolean = false,
    isLoading: Boolean = false,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = buttonIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isOutlinedButton) {
                OutlinedButton(
                    onClick = onButtonClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .bounceClick(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(
                        buttonIcon,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(buttonText)
                }
            } else {
                ElevatedButton(
                    onClick = onButtonClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .bounceClick(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(
                        buttonIcon,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(buttonText)
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}