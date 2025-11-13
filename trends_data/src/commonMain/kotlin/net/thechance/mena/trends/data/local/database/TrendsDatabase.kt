package net.thechance.mena.trends.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [UserEngagement::class], version = 1)
@ConstructedBy(TrendsDatabaseConstructor::class)
abstract class TrendsDatabase : RoomDatabase() {
    abstract fun userEngagementDao(): UserEngagementDao
}

@Suppress("KotlinNoActualForExpect")
expect object TrendsDatabaseConstructor : RoomDatabaseConstructor<TrendsDatabase> {
    override fun initialize(): TrendsDatabase
}