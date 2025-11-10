package net.thechance.mena.trends.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.annotation.Single
import platform.Foundation.NSHomeDirectory

@Single
actual class TrendsDatabaseBuilder actual constructor() {
    actual fun getBuilder(): RoomDatabase.Builder<TrendsDatabase> {
        val dbFile = NSHomeDirectory() + "/$DATABASE_NAME"
        return Room.databaseBuilder<TrendsDatabase>(
            name = dbFile,
        )
    }
}