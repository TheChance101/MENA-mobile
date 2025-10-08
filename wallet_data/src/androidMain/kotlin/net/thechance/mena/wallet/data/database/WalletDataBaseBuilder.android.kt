package net.thechance.mena.wallet.data.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun getDatabaseBuilder(context: Context): WalletDatabase {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("wallet.db")
    return Room.databaseBuilder<WalletDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .build()
}