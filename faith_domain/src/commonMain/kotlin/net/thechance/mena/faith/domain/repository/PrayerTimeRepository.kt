package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
@OptIn(ExperimentalTime::class)
interface PrayerTimeRepository {
    suspend fun getPrayerTimes(date: Instant, location: Location): List<PrayerTime>
}

