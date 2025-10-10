package net.thechance.mena.wallet.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase


actual class WalletDatabaseBuilder() {
    actual fun getBuilder(context:Any?): RoomDatabase.Builder<WalletDatabase> {
        val appContext = context as Context
        val dbFile = appContext.getDatabasePath(DataBaseConfig.DATABASE_NAME)
        return Room.databaseBuilder<WalletDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}