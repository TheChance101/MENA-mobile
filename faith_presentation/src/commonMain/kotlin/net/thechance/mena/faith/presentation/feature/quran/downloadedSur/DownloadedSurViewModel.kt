package net.thechance.mena.faith.presentation.feature.quran.downloadedSur

import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.surah_deleted_successfully
import mena.faith_presentation.generated.resources.surah_deleted_successfully_downloading
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel

class DownloadedSurViewModel(
    private val quranRepository: QuranRepository,
) : BaseViewModel<DownloadedSurUiState, DownloadedSurEffect>(
    initialState = DownloadedSurUiState(),
), DownloadedSurInteractionListener {

    init {
        loadDownloadedSur()
    }

    private fun loadDownloadedSur() {
        tryToCollect(
            block = { quranRepository.getDownloadedSur() },
            onEmitNewValue = { downloadedSur ->
                updateState {
                    it.copy(
                        surDetails = downloadedSur.map { surah -> surah.toUiState() }
                    )
                }
            },
            onError = ::handleErrorSnackBar
        )
    }


    override fun onReciterSettingsClick() {
        val surahId = uiState.value.selectedSurahForDelete
            ?: uiState.value.surDetails.firstOrNull()?.id
            ?: 1

        sendEffect(DownloadedSurEffect.NavigateToRecitersScreen(surahId))
    }

    override fun onDownloadedSurahClick(surahId: Int) =
        sendEffect(DownloadedSurEffect.NavigateToDownloadedSurahReciterScreen(surahId))

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
        handleSuccessSnackBar(Res.string.surah_deleted_successfully_downloading)
    }

    override fun onDismissDeleteConfirmationDialog() {
        updateState { it.copy(selectedSurahForDelete = null, showDeleteConfirmationDialog = false) }
    }

    override fun onConfirmDeleteDownloadedSurahClick() {
        val surahId = uiState.value.selectedSurahForDelete
        tryToExecute(
            execute = {
                surahId?.let {
                    quranRepository.deleteSurahAudioByReciter(surahId)
                }
            },
            onSuccess = {
                onDeleteSurahSuccess()
            },
            onError = ::handleErrorSnackBar
        )
    }

    private fun onDeleteSurahSuccess() {
        updateState { state ->
            val newSurDetails =
                state.surDetails - state.surDetails.first { it.id == state.selectedSurahForDelete }

            state.copy(
                surDetails = newSurDetails,
                selectedSurahForDelete = null,
                showDeleteConfirmationDialog = false
            )
        }

        handleSuccessSnackBar(Res.string.surah_deleted_successfully)
    }

}
