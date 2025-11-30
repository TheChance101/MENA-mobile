package net.thechance.mena.faith.presentation.utils.extentions

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key.Companion.R
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.reciter_name_type
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.DownloadedSurUiState
import net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters.DownloadedReciterItemUi
import net.thechance.mena.faith.presentation.feature.quran.surah.ReciterUiState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReciterUiState.localizedName(): String {
    val type = stringResource(Res.string.reciter_name_type,)
    return when (type) {
        "ar" -> arabicName
        else -> name
    }
}

@Composable
fun DownloadedReciterItemUi.localizedName(): String {
    val type = stringResource(Res.string.reciter_name_type,)
    return when (type) {
        "ar" -> arabicName
        else -> name
    }
}

@Composable
fun DownloadedSurUiState.SurahDetailsUiState.localizedName(): List<String> {
    val type = stringResource(Res.string.reciter_name_type,)
    return when (type) {
        "ar" -> reciterArabicName
        else -> recitersName
    }
}
