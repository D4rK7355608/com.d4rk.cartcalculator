package com.d4rk.cartcalculator.app.settings.cart.utils.helpers

import android.content.Context
import android.net.Uri
import com.d4rk.cartcalculator.app.settings.cart.backup.domain.data.model.AppBackupData
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// FIXME: MAke this class backup all data from our tables
object DatabaseBackupHelper {

    private const val TAG = "DatabaseBackupHelper"
    private const val BACKUP_FILE_PREFIX = "cart_backup_"
    private const val BACKUP_FILE_EXTENSION = ".json"

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    suspend fun createBackup(
        context: Context,
        database: DatabaseInterface,
        outputFileUri: Uri? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            println("$TAG: Starting database backup...")
            val allCarts = database.getAllCarts()
            val allCartItems = mutableListOf<ShoppingCartItemsTable>()
            allCarts.forEach { cart ->
                allCartItems.addAll(database.getItemsByCartId(cart.cartId))
            }

            val backupData = AppBackupData(carts = allCarts, cartItems = allCartItems)
            val jsonString = json.encodeToString(backupData)

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "$BACKUP_FILE_PREFIX$timestamp$BACKUP_FILE_EXTENSION"

            val filePath: String

            if (outputFileUri != null) {
                context.contentResolver.openOutputStream(outputFileUri)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                    filePath = outputFileUri.path ?: fileName
                    println("$TAG: Backup successfully written to SAF URI: $outputFileUri")
                } ?: throw Exception("Failed to open output stream for SAF URI.")
            } else {

                val backupDir = File(context.getExternalFilesDir(null), "backups")
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }
                val backupFile = File(backupDir, fileName)
                FileOutputStream(backupFile).use { fos ->
                    fos.write(jsonString.toByteArray())
                }
                filePath = backupFile.absolutePath
                println("$TAG: Backup successfully created at: $filePath")
            }
            filePath
        }.onFailure { e ->
            println("$TAG: Error during database backup: ${e.message}")
        }
    }

    suspend fun restoreBackup(
        context: Context,
        database: DatabaseInterface,
        inputFileUri: Uri
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            println("$TAG: Starting database restore (add/update mode) from URI: $inputFileUri")

            val jsonString =
                context.contentResolver.openInputStream(inputFileUri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        reader.readText()
                    }
                } ?: throw Exception("Failed to open input stream for SAF URI or file is empty.")

            if (jsonString.isBlank()) {
                throw Exception("Backup file is empty.")
            }

            val backupData = json.decodeFromString<AppBackupData>(jsonString)

            println("$TAG: Inserting/Updating ${backupData.carts.size} carts...")

            for (cart in backupData.carts) {
                database.insertCart(cart)
            }

            println("$TAG: Inserting/Updating ${backupData.cartItems.size} cart items...")

            for (item in backupData.cartItems) {
                database.insertItem(item)
            }

            println("$TAG: Database restore (add/update mode) completed successfully.")
        }.onFailure { e ->
            println("$TAG: Error during database restore: ${e.message}")

        }
    }
}