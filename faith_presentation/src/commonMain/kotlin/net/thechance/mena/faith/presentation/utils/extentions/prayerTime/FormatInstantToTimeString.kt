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

fun getHijriReadableDate(prayerTimes: List<PrayerTime>): String = runCatching {
    val hijriDate = prayerTimes.firstOrNull()?.hijriDate ?: return ""
    val parts = hijriDate.split("-")
    if (parts.size != 3) return hijriDate
    val day = parts[DAY_INDEX].toInt()
    val month = parts[MONTH_INDEX].toInt()
    val year = parts[YEAR_INDEX].toInt()
    val monthName = hijriMonths[month]
    "$day $monthName $year"
}.getOrDefault("")

@OptIn(ExperimentalTime::class)
fun getSunriseTime(prayerTimes: List<PrayerTime>): String =
    prayerTimes
        .firstOrNull { it.name == PrayerName.SUNRISE }
        ?.let { formatInstantToTimeString(it.time) } ?: ""

private val hijriMonths: Map<Int, String> = mapOf(
    1 to "Muharram",
    2 to "Safar",
    3 to "Rabi Al-Awwal",
    4 to "Rabi Al-Akhar",
    5 to "Jumada Al-Awwal",
    6 to "Jumada Al-Akhirah",
    7 to "Rajab",
    8 to "Shaban",
    9 to "Ramadan",
    10 to "Shawwal",
    11 to "Dhul Qadah",
    12 to "Dhul Hijjah"
)

private const val DAY_INDEX = 0
private const val MONTH_INDEX = 1
private const val YEAR_INDEX = 2
