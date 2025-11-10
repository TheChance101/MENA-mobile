package net.thechance.mena.trends.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [/* TODO: add entities */User::class],
    version = 1,
    exportSchema = true
)
abstract class TrendsDatabase : RoomDatabase() {
    // TODO: add DAOs
}