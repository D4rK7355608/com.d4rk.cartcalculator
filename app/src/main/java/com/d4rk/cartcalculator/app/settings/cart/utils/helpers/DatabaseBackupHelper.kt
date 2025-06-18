package com.d4rk.cartcalculator.app.settings.cart.utils.helpers

import android.content.Context
import android.net.Uri
import com.d4rk.cartcalculator.app.settings.cart.backup.domain.data.model.AppBackupData
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartItemsTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    @OptIn(ExperimentalSerializationApi::class)
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

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "$BACKUP_FILE_PREFIX$timestamp$BACKUP_FILE_EXTENSION"

            val filePath: String

            if (outputFileUri != null) {
                context.contentResolver.openOutputStream(outputFileUri)?.use { outputStream ->
                    json.encodeToStream(backupData, outputStream)
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
                    json.encodeToStream(backupData, fos)
                }
                filePath = backupFile.absolutePath
                println("$TAG: Backup successfully created at: $filePath")
            }
            filePath
        }.onFailure { e ->
            println("$TAG: Error during database backup: ${e.message}")
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun restoreBackup(
        context: Context,
        database: DatabaseInterface,
        inputFileUri: Uri
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            println("$TAG: Starting database restore (add/update mode) from URI: $inputFileUri")

            val backupData = context.contentResolver.openInputStream(inputFileUri)?.use { inputStream ->
                json.decodeFromStream<AppBackupData>(inputStream)
            } ?: throw Exception("Failed to open input stream for SAF URI or file is empty.")

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