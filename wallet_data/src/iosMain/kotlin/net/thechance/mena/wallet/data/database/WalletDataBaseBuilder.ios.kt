package net.thechance.mena.wallet.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class WalletDatabaseBuilder {
    actual fun getBuilder(context: Any?): RoomDatabase.Builder<WalletDatabase> {
        val dbFilePath = documentDirectory() + "/${DataBaseConfig.DATABASE_NAME}"
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
