package net.thechance.mena.wallet.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.annotation.Single
import org.koin.core.context.GlobalContext

@Single
actual class WalletDatabaseBuilder actual constructor() {
    actual fun getBuilder(): RoomDatabase.Builder<WalletDatabase> {
        val appContext = GlobalContext.get().get<Context>()
        val dbFile = appContext.getDatabasePath(DATABASE_NAME)
        return Room.databaseBuilder<WalletDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}

private const val DATABASE_NAME = "wallet.db"