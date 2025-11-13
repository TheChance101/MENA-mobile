package net.thechance.mena.trends.data.local.database

import androidx.room.RoomDatabase
import org.koin.core.annotation.Single

const val DATABASE_NAME = "trends_database.db"

@Single
expect class TrendsDatabaseBuilder() {
    fun getBuilder(): RoomDatabase.Builder<TrendsDatabase>
}