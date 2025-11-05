package net.thechance.mena.faith.presentation.utils.extentions.prayerTime

import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun getSunriseTime(prayerTimes: List<PrayerTime>): String =
    prayerTimes
        .firstOrNull { it.name == PrayerName.SUNRISE }
        ?.let { it.time.formatInstantToTimeString(withISPM = false) } ?: ""
