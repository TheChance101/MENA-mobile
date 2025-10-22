package net.thechance.mena.faith.presentation.utils.extentions.prayerTime

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun formatInstantToTimeString(instant: Instant): String {
    val instant = Instant.fromEpochMilliseconds(instant.toEpochMilliseconds())
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val hour =
        localDateTime.hour
            .let { if (it > 12) it - 12 else it }
            .toString()
            .padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')

    return "$hour:$minute"
}

fun getHijriDate(prayerTimes: List<PrayerTime>): String =
    prayerTimes.firstOrNull()?.hijriDate ?: ""


@OptIn(ExperimentalTime::class)
fun getSunriseTime(prayerTimes: List<PrayerTime>): String =
    prayerTimes
        .firstOrNull { it.name == PrayerName.SUNRISE }
        ?.let { formatInstantToTimeString(it.time) } ?: ""