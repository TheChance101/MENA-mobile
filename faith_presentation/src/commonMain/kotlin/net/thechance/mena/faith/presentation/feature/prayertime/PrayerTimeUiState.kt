package net.thechance.mena.faith.presentation.feature.prayertime

import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class PrayerTimeUiState(
    val prayerTimes: List<PrayerTime> = emptyList(),
    val nextPrayerCountdown: String = "01:32:56",
    val nextPrayerName: PrayerName = PrayerName.DHUHR,
    val currentDate: String = "22 Sufar 1447H",
)