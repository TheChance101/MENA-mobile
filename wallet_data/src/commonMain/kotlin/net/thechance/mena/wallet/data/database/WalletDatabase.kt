package net.thechance.mena.wallet.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import net.thechance.mena.wallet.data.database.Statement

@Database(entities = [Statement::class], version = 1)
abstract class WalletDatabase : RoomDatabase() {
    abstract fun getStatementDao(): StatementDao
}

@ConstructedBy(WalletDatabaseConstructor::class)
@Suppress("KotlinNoActualForExpect")
expect object WalletDatabaseConstructor : RoomDatabaseConstructor<WalletDatabase> {
    override fun initialize(): WalletDatabase
}