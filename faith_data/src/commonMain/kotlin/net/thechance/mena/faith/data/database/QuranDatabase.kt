package net.thechance.mena.faith.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import net.thechance.mena.faith.data.database.prayertimes.PrayerTimesDao
import net.thechance.mena.faith.data.database.prayertimes.PrayerTimesLocal

@Database(
    entities = [
        AyahDto::class,
        SurahAudioDto::class,
        ReciterDto::class,
        PrayerTimesLocal::class
    ],
    version = 1
)
@TypeConverters(DateTimeConverters::class)
@ConstructedBy(QuranDatabaseConstructor::class)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun getAyaDao(): AyahDao

    abstract fun getSurahAudioDao(): SurahAudioDao

    abstract fun getRecitersDao(): RecitersDao

    abstract fun getPrayerTimesDao(): PrayerTimesDao
}

@Suppress("KotlinNoActualForExpect")
expect object QuranDatabaseConstructor : RoomDatabaseConstructor<QuranDatabase> {
    override fun initialize(): QuranDatabase
}
