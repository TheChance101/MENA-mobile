package net.thechance.mena.faith.presentation.feature.main

import androidx.compose.ui.graphics.painter.Painter
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import org.jetbrains.compose.resources.StringResource

data class MainUiState(
    val isLoading: Boolean = false,
    val prayerTimes: List<PrayerTime> = emptyList(),
    val tilawahUiState: TilawahUiState? = null,
    val prayerTimesUiState: PrayerTimesUiState? = null,
    val hijriDate: String = "",
    val sunriseTime: String = ""
)

data class PrayerTimesUiState(
    val prayers: List<PrayerUiModel>,
    val nextPrayerIndex: Int
)

data class TilawahUiState(
    val surahName: String,
    val ayahNumber: Int,
    val surahId: Int
)

data class PrayerUiModel(
    val name: PrayerName,
    val displayName: StringResource,
    val time: String,
    val isAM: Boolean
)

data class FeatureItem(
    val title: String,
    val icon: Painter,
    val onClick: () -> Unit
)