package net.thechance.mena.faith.presentation.utils.extentions

import androidx.compose.runtime.Composable
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.reciter_name_type
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.DownloadedSurUiState
import net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters.DownloadedReciterItemUi
import net.thechance.mena.faith.presentation.feature.quran.reciter.reciterSelection.ReciterSearchItemUi
import net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen.SurahRecitersUiState
import net.thechance.mena.faith.presentation.feature.quran.surah.ReciterUiState
import org.jetbrains.compose.resources.stringResource

@Composable
private fun getLocaleType(): String = stringResource(Res.string.reciter_name_type)

@Composable
private fun selectLocalizedName(
    arabicName: String,
    englishName: String
): String {
    return when (getLocaleType()) {
        "ar" -> arabicName
        else -> englishName
    }
}

@Composable
fun SurahRecitersUiState.ReciterUi.toLocalizedName(): String {
    return selectLocalizedName(arabicName, name)
}

@Composable
fun ReciterSearchItemUi.toLocalizedName(): String {
    return selectLocalizedName(arabicName, name)
}

@Composable
fun ReciterUiState.localizedName(): String {
    return selectLocalizedName(arabicName, name)
}

@Composable
fun DownloadedReciterItemUi.localizedName(): String {
    return selectLocalizedName(arabicName, name)
}

@Composable
fun DownloadedSurUiState.SurahDetailsUiState.localizedName(): List<String> {
    return when (getLocaleType()) {
        "ar" -> reciterArabicName
        else -> recitersName
    }
}