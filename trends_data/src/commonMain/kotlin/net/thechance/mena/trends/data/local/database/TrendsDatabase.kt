package net.thechance.mena.trends.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEngagement::class],
    version = 1,
    exportSchema = true
)
abstract class TrendsDatabase : RoomDatabase() {
    // TODO: add DAOs
}