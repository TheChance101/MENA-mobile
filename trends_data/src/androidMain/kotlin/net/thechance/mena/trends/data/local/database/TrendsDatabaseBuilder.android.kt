package net.thechance.mena.trends.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.annotation.Single
import org.koin.core.context.GlobalContext

@Single
actual class TrendsDatabaseBuilder actual constructor() {
    actual fun getBuilder(): RoomDatabase.Builder<TrendsDatabase> {
        val appContext = GlobalContext.get().get<Context>()
        val dbFile = appContext.getDatabasePath(DATABASE_NAME)
        return Room.databaseBuilder<TrendsDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}