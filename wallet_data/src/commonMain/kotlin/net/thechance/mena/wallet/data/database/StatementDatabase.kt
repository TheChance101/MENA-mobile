package net.thechance.mena.wallet.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import net.thechance.mena.wallet.data.database.entity.Statement

@Database(entities = [Statement::class], version = 1)
abstract class StatementDatabase : RoomDatabase() {
    abstract fun statementDao(): StatementDao
}