package net.thechance.mena.faith.presentation.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_kaaba
import mena.faith_presentation.generated.resources.ic_mosque
import mena.faith_presentation.generated.resources.ic_quran
import mena.faith_presentation.generated.resources.ic_tilawah
import mena.faith_presentation.generated.resources.nearby_mosques
import mena.faith_presentation.generated.resources.qiblah_direction
import mena.faith_presentation.generated.resources.quran_kareem
import mena.faith_presentation.generated.resources.tilawah
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

data class MainUiState(
    val isLoading: Boolean = false,
    val prayerTimes: List<PrayerTime> = emptyList(),
    val tilawahUiState: TilawahUiState? = null,
    val prayerTimesUiState: PrayerTimesUiState? = null,
    val hijriDate: String = "",
    val sunriseTime: String = "",
    val address: String = ""
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

@Composable
fun faithFeatureCards(listener: MainInteractionListener): List<FeatureItem> {
    return listOf(
        FeatureItem(
            title = stringResource(Res.string.quran_kareem),
            icon = painterResource(Res.drawable.ic_quran),
            onClick = listener::onQuranClick
        ),
        FeatureItem(
            title = stringResource(Res.string.qiblah_direction),
            icon = painterResource(Res.drawable.ic_kaaba),
            onClick = listener::onQiblahClick
        ),
        FeatureItem(
            title = stringResource(Res.string.nearby_mosques),
            icon = painterResource(Res.drawable.ic_mosque),
            onClick = listener::onMosquesClick
        ),
        FeatureItem(
            title = stringResource(Res.string.tilawah),
            icon = painterResource(Res.drawable.ic_tilawah),
            onClick = listener::onTilawahClick,
        ),
    )
}
