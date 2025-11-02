package net.thechance.mena.faith.presentation.utils.extentions.prayerTime

import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun findNextPrayer(
    prayerTimes: List<PrayerTime>,
    currentTime: kotlin.time.Instant
): PrayerTime? {
    val sortedPrayers = prayerTimes.sortedBy { it.time }
    val nextPrayer = sortedPrayers.firstOrNull { it.time > currentTime }

    return nextPrayer ?: sortedPrayers.firstOrNull()
}
