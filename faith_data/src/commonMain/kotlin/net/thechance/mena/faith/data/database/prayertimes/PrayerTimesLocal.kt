package net.thechance.mena.faith.data.database.prayertimes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Entity(tableName = "prayer_times")
data class PrayerTimesLocal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "saved_in")
    val savedInDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),

    @ColumnInfo(name = "date")
    val date: LocalDate,

    @ColumnInfo(name = "hijri_date")
    val hijriDate: String,

    @ColumnInfo(name = "fajr")
    val fajr: String,

    @ColumnInfo(name = "sunrise")
    val sunrise: String,

    @ColumnInfo(name = "dhuhr")
    val dhuhr: String,

    @ColumnInfo(name = "asr")
    val asr: String,

    @ColumnInfo(name = "maghrib")
    val maghrib: String,

    @ColumnInfo(name = "isha")
    val isha: String
)
