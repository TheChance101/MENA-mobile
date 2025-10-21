package net.thechance.mena.wallet.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.annotation.Single
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@Single
actual class WalletDatabaseBuilder actual constructor() {
    actual fun getBuilder(): RoomDatabase.Builder<WalletDatabase> {
        val dbFilePath = documentDirectory() + "/${DATABASE_NAME}"
        return Room.databaseBuilder<WalletDatabase>(
            name = dbFilePath
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    return requireNotNull(url?.path)
}

private const val DATABASE_NAME = "wallet.db"