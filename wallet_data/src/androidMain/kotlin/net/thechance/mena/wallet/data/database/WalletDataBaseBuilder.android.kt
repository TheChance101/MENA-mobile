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
        val dbFile = appContext.getDatabasePath(DataBaseConfig.DATABASE_NAME)
        return Room.databaseBuilder<WalletDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}