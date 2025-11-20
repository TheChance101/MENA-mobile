package net.thechance.mena.faith.presentation.feature.prayertime

import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.presentation.utils.IslamicDate
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalTime::class)
data class PrayerTimeUiState @OptIn(ExperimentalUuidApi::class) constructor(
    val prayerTimes: List<PrayerTime> = emptyList(),
    val nextPrayerCountdown: String = "",
    val nextPrayerName: PrayerName? = null,
    val currentDate: IslamicDate = IslamicDate(1,1,1447),
    val nextPrayerTime: Instant = Instant.fromEpochMilliseconds(0),
    val isDatePickerShown: Boolean = false,
    val islamicDatePickerUiState: IslamicDatePickerUiState = IslamicDatePickerUiState(),
    val isTodayPrayer: Boolean = true,
    val address: String = "",
) {
    data class IslamicDatePickerUiState(
        val selectedIslamicDate: IslamicDate = IslamicDate(1,1,1447),
        val isClearDateActive: Boolean = false,
    )
}