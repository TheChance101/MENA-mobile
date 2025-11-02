package net.thechance.mena.faith.presentation.feature.downloadedSur

import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_ad_duha
import mena.faith_presentation.generated.resources.ic_al_kahf
import mena.faith_presentation.generated.resources.ic_an_nas
import mena.faith_presentation.generated.resources.ic_ash_shams
import net.thechance.mena.faith.presentation.base.BaseViewModel

class DownloadedSurViewModel :
    BaseViewModel<DownloadedSurUiState, DownloadedSurEffect>(
        initialState = DownloadedSurUiState(),
    ),
    DownloadedSurInteractionListener {
    init {
        loadDownloadedSur()
    }

    private fun loadDownloadedSur() {
        // TODO: After the domain is done, integrate this function to load the real data
        val dummyData = listOf(
            DownloadedSurUiState.SurahDetailsUiState(
                1,
                Res.drawable.ic_ad_duha,
                "Al-Duha",
                listOf("Al Minshawi", "Sudais"),
            ),
            DownloadedSurUiState.SurahDetailsUiState(
                1,
                Res.drawable.ic_an_nas,
                "An-Nas",
                listOf("Sudais"),
            ),
            DownloadedSurUiState.SurahDetailsUiState(
                1,
                Res.drawable.ic_al_kahf,
                "Al-Kahf",
                listOf("Al Minshawi", "Sudais"),
            ),
            DownloadedSurUiState.SurahDetailsUiState(
                1,
                Res.drawable.ic_ash_shams,
                "Ash-Shams",
                listOf("Al Minshawi", "Sudais"),
            ),
        )
        updateState { it.copy(dummyData) }
    }

    override fun onReciterSettingsClick() {
        sendEffect(DownloadedSurEffect.NavigateToRecitersScreen)
    }

    override fun onDownloadedSurahClick(surahId: Int) {
        // TODO("Integrate with the domain repo when done")
    }

    override fun onDeleteDownloadedSurahClick(surahId: Int) {
        // TODO("Integrate with the domain repo when done")
    }

    override fun onBackClick() {
        sendEffect(DownloadedSurEffect.NavigateBack)
    }
}
