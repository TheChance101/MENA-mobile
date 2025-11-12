package net.thechance.mena.faith.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [AyahDto::class, SurahAudioDto::class, ReciterDto::class], version = 1)
@ConstructedBy(QuranDatabaseConstructor::class)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun getAyaDao(): AyahDao
    abstract fun getSurahAudioDao(): SurahAudioDao
    abstract fun getRecitersDao(): RecitersDao
}

@Suppress("KotlinNoActualForExpect")
expect object QuranDatabaseConstructor : RoomDatabaseConstructor<QuranDatabase> {
    override fun initialize(): QuranDatabase
}