package net.thechance.mena.faith.presentation.feature.prayertime

import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class PrayerTimeUiState(
    val prayerTimes: List<PrayerTime> = listOf(
        PrayerTime(
            name = PrayerName.FAJR,
            time = Instant.fromEpochSeconds(1704067920),
            hijriDate = "22 Sufar 1447H"
        ),
        PrayerTime(
            name = PrayerName.DHUHR,
            time = Instant.fromEpochSeconds(1704110400),
            hijriDate = "22 Sufar 1447H"
        ),
        PrayerTime(
            name = PrayerName.ASR,
            time = Instant.fromEpochSeconds(1704124800),
            hijriDate = "22 Sufar 1447H"
        ),
        PrayerTime(
            name = PrayerName.MAGHRIB,
            time = Instant.fromEpochSeconds(1704138060),
            hijriDate = "22 Sufar 1447H"
        ),
        PrayerTime(
            name = PrayerName.ISHA,
            time = Instant.fromEpochSeconds(1704144540),
            hijriDate = "22 Sufar 1447H"
        )
    ),
    val nextPrayerCountdown: String = "01:32:56",
    val nextPrayerName: PrayerName = PrayerName.DHUHR,
    val currentDate: String = "22 Sufar 1447H",
)