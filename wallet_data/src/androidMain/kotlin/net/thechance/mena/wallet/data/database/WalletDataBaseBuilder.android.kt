package net.thechance.mena.wallet.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase


fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<WalletDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("wallet.db")
    return Room.databaseBuilder<WalletDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}