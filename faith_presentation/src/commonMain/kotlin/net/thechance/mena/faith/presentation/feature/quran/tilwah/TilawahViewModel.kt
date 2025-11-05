package net.thechance.mena.faith.presentation.feature.quran.tilwah

import kotlinx.coroutines.flow.first
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.ErrorState

class TilawahViewModel(val quranRepository: QuranRepository) :
    BaseViewModel<TilawahUiState, TilawahEffect>(
        TilawahUiState()
    ), TilawahInteractionListener {

    init {
        getAllReciters()
        updateDefaultReciter()
    }

    private fun updateDefaultReciter() {
        tryToExecute(
            execute = { quranRepository.getDefaultReciter() },
            onSuccess = { reciterId -> updateSelectedReciter(reciterId.first()) },
            onError = ::handleError
        )
    }

    override fun onBackClick() = sendEffect(TilawahEffect.NavigateBack)
    override fun onSearchClick() = sendEffect(TilawahEffect.NavigateToSearch)
    override fun onSelectReciterClick(reciterId: Int) {
        tryToExecute(
            execute = { quranRepository.saveDefaultReciter(reciterId) },
            onSuccess = { updateSelectedReciter(reciterId) },
            onError = ::handleError
        )
    }

    private fun getAllReciters() {
        tryToExecute(
            execute = { quranRepository.getReciters() },
            onSuccess = ::getAllRecitersSuccessfully,
        )
    }

    private fun updateSelectedReciter(reciterId: Int) {
        updateState { state ->
            state.copy(
                selectedReciterId = reciterId,
            )
        }
    }

    private fun handleError(errorState: ErrorState) {
        println("Error: $errorState")
    }

    private fun getAllRecitersSuccessfully(reciters: List<Reciter>) {
        val recitersUi = reciters.map {
            ReciterUi(
                id = it.id,
                name = it.name,
                recitingType = it.tilawahType,
                isDownloaded = false //TODO NOT IMPLEMENTED YET
            )
        }
        updateState { it.copy(reciters = recitersUi) }
    }
}
