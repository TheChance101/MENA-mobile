package net.thechance.mena.faith.data.repository

import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class PrayerTimeRepositoryImpl : PrayerTimeRepository {

    @OptIn(ExperimentalTime::class)
    override suspend fun getPrayerTimes(
        date: Instant,
        location: Location
    ): List<PrayerTime> {
        val baseDay = date
        val hijriDate = "22 Safar 1447H"

        return listOf(
            PrayerTime(
                name = PrayerName.FAJR,
                time = baseDay + 4.hours + 15.minutes,
                hijriDate = hijriDate
            ),
            PrayerTime(
                name = PrayerName.SUNRISE,
                time = baseDay + 6.hours + 45.minutes,
                hijriDate = hijriDate
            ),
            PrayerTime(
                name = PrayerName.DHUHR,
                time = baseDay + 12.hours + 5.minutes,
                hijriDate = hijriDate
            ),
            PrayerTime(
                name = PrayerName.ASR,
                time = baseDay + 15.hours + 32.minutes,
                hijriDate = hijriDate
            ),
            PrayerTime(
                name = PrayerName.MAGHRIB,
                time = baseDay + 18.hours + 2.minutes,
                hijriDate = hijriDate
            ),
            PrayerTime(
                name = PrayerName.ISHA,
                time = baseDay + 19.hours + 25.minutes,
                hijriDate = hijriDate
            )
        )
    }
}
