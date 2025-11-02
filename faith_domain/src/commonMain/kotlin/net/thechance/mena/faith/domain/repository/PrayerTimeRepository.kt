package net.thechance.mena.faith.domain.repository

import kotlinx.datetime.TimeZone
import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
interface PrayerTimeRepository {
    suspend fun getPrayerTimes(
        date: Instant,
        location: Location,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): List<PrayerTime>

    suspend fun getPrayerTimeInHijriDate(
        date: String,
        location: Location,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): List<PrayerTime>
}
