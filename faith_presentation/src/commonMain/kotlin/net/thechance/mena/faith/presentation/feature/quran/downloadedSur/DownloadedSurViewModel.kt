package net.thechance.mena.faith.presentation.feature.quran.downloadedSur

import androidx.lifecycle.viewModelScope
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_ad_duha
import mena.faith_presentation.generated.resources.ic_al_kahf
import mena.faith_presentation.generated.resources.ic_an_nas
import mena.faith_presentation.generated.resources.ic_ash_shams
import mena.faith_presentation.generated.resources.surah_deleted_successfully
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler

class DownloadedSurViewModel(
    snackBarHandler: SnackbarHandler,
) :
    BaseViewModel<DownloadedSurUiState, DownloadedSurEffect>(
        initialState = DownloadedSurUiState(),
        snackbarHandler = snackBarHandler,
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
                2,
                Res.drawable.ic_an_nas,
                "An-Nas",
                listOf("Sudais"),
            ),
            DownloadedSurUiState.SurahDetailsUiState(
                3,
                Res.drawable.ic_al_kahf,
                "Al-Kahf",
                listOf("Al Minshawi", "Sudais"),
            ),
            DownloadedSurUiState.SurahDetailsUiState(
                4,
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

    override fun onBackClick() {
        sendEffect(DownloadedSurEffect.NavigateBack)
    }

    override fun onDeleteSurahClick(surahId: Int) {
        updateState {
            it.copy(
                selectedSurahForDelete = surahId,
                showDeleteConfirmationDialog = true,
            )
        }
    }

    override fun onDismissDeleteConfirmationDialog() {
        updateState { it.copy(selectedSurahForDelete = null, showDeleteConfirmationDialog = false) }
    }

    override fun onConfirmDeleteDownloadedSurahClick() {
        // TODO("Should integrate with the domain to delete selected surah")
        updateState { state ->
            val newSurDetails =
                state.surDetails - state.surDetails.first { it.id == state.selectedSurahForDelete }
            state.copy(
                surDetails = newSurDetails,
                selectedSurahForDelete = null,
                showDeleteConfirmationDialog = false,
            )
        }
        showSuccessSnackBar()
    }

    private fun showSuccessSnackBar() = snackbarHandler.showSnackBar(
        message = Res.string.surah_deleted_successfully,
        status = SnackBarState.Status.Success,
        scope = viewModelScope,
    )
}
