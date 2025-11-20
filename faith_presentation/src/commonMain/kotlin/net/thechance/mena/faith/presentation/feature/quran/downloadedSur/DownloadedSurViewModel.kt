package net.thechance.mena.faith.presentation.feature.quran.downloadedSur

import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.surah_deleted_successfully
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
        tryToExecute(
            execute = { quranRepository.getDownloadedSur() },
            onSuccess = { downloadedSurah ->
                updateState {
                    it.copy(
                        surDetails = downloadedSurah.map { surah -> surah.toUiState() }
                    )
                }
            }
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
    }

    override fun onDismissDeleteConfirmationDialog() {
        updateState { it.copy(selectedSurahForDelete = null, showDeleteConfirmationDialog = false) }
    }

    override fun onConfirmDeleteDownloadedSurahClick() {
        val surahId = uiState.value.selectedSurahForDelete
        tryToExecute(
            execute = {
                surahId?.let {
                    quranRepository.deleteSurahWithSpecificReciter(surahId)
                }
            },
            onSuccess = {
                onDeleteSurahSuccess()
            }
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
