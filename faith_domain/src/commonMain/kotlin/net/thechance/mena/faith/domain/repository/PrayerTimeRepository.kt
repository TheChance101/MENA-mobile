package net.thechance.mena.faith.domain.repository

import kotlinx.datetime.TimeZone
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.identity.domain.entity.Address
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
interface PrayerTimeRepository {
    suspend fun getPrayerTimes(
        date: Instant,
        address: Address,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): List<PrayerTime>

    suspend fun getPrayerTimesByHijriDate(
        date: String,
        address: Address,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
        isHijri: Boolean
    ): List<PrayerTime>
}
