package net.thechance.mena.faith.presentation.feature.main

import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import org.jetbrains.compose.resources.StringResource

data class MainScreenState(
    val isLoading: Boolean = false,
    val prayerTimes: List<PrayerTime> = emptyList(),
    val tilawahUiState: TilawahUiState? = null,
    val prayerTimesUiState: PrayerTimesUiState? = null,
    val hijriDate: String = "",
    val sunriseTime: String = ""
)

data class PrayerTimesUiState(
    val prayers: List<PrayerUiModel>,
    val currentPrayerIndex: Int
)

data class TilawahUiState(
    val surahName: String,
    val ayahNumber: String,
    val surahId: Int
)

data class PrayerUiModel(
    val name: PrayerName,
    val displayName: StringResource,
    val time: String,
    val isAM: Boolean
)