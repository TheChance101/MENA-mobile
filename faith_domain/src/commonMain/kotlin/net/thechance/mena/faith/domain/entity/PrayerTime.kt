package net.thechance.mena.faith.domain.entity

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
@OptIn(ExperimentalTime::class)
data class PrayerTime(
    val name: PrayerName,
    val time: Instant,
    val hijriDate: String
)

enum class PrayerName {
    FAJR,
    SUNRISE,
    DHUHR,
    ASR,
    MAGHRIB,
    ISHA
}