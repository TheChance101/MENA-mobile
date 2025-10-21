package net.thechance.mena.wallet.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import net.thechance.mena.wallet.data.dto.local.LocalStatement

@Database(entities = [LocalStatement::class], version = 1)
@ConstructedBy(WalletDatabaseConstructor::class)
abstract class WalletDatabase : RoomDatabase() {
    abstract fun getStatementDao(): StatementDao
}

@Suppress("KotlinNoActualForExpect")
expect object WalletDatabaseConstructor : RoomDatabaseConstructor<WalletDatabase> {
    override fun initialize(): WalletDatabase
}