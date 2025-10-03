package net.thechance.mena.faith.data.database

import androidx.room.RoomDatabase
import net.thechance.mena.faith_data.Res

expect class QuranDatabaseBuilder {
    internal val pathPrefix:String
    fun getBuilder(): RoomDatabase.Builder<QuranDatabase>
}

internal const val DATABASE_NAME = "hafs_smart_v8.db"
internal val databaseUri = Res.getUri("files/$DATABASE_NAME")
