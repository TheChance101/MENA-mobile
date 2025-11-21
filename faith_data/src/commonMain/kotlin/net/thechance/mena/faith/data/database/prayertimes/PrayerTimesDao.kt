package net.thechance.mena.faith.data.database.prayertimes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime

@Dao
interface PrayerTimesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPrayerTimes(prayerTimes: PrayerTimesLocal)

    @Query("SELECT * FROM prayer_times WHERE latitude = :latitude AND longitude = :longitude AND date = :date LIMIT 1")
    suspend fun getPrayerTimes(
        latitude: Double,
        longitude: Double,
        date: LocalDate
    ): PrayerTimesLocal

    @Query("SELECT * FROM prayer_times WHERE latitude = :latitude AND longitude = :longitude AND hijri_date = :hijriDate")
    suspend fun getPrayerTimesByHijriDate(
        latitude: Double,
        longitude: Double,
        hijriDate: String
    ): PrayerTimesLocal

    @OptIn(ExperimentalTime::class)
    @Query("DELETE FROM prayer_times WHERE saved_in < :expiredDate")
    suspend fun deleteExpiredPrayerTimes(expiredDate: LocalDate)
}
