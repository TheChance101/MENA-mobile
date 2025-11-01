package net.thechance.mena.faith.presentation.feature.prayertime

import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class PrayerTimeUiState(
    val prayerTimes: List<PrayerTime> = emptyList(),
    val nextPrayerCountdown: String = "",
    val nextPrayerName: PrayerName = PrayerName.DHUHR,
    val currentDate: String = "",
    val city: String = ""
)