package net.thechance.mena.faith.presentation.utils.extentions.prayerTime

import net.thechance.mena.faith.domain.entity.PrayerTime

fun getHijriReadableDate(prayerTimes: List<PrayerTime>): String {
    val hijriDate = prayerTimes.firstOrNull()?.hijriDate ?: return ""
    val parts = hijriDate.split("-")
    if (parts.size != 3) return hijriDate
    val day = parts[DAY_INDEX].toInt()
    val month = parts[MONTH_INDEX].toInt()
    val year = parts[YEAR_INDEX].toInt()
    val monthName = hijriMonths[month] ?: return hijriDate
    return "$day $monthName $year"
}

fun getHijriDay(prayerTime: PrayerTime): Int {
    val parts = prayerTime.hijriDate.split("-")
    if (parts.size != 3) return 0
    return parts[DAY_INDEX].toInt()
}

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