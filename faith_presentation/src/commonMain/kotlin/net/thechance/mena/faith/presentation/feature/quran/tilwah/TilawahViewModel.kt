package net.thechance.mena.faith.presentation.feature.quran.tilwah

import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.ErrorState

class TilawahViewModel(
    private val quranRepository: QuranRepository,
) : BaseViewModel<TilawahUiState, TilawahEffect>(
    TilawahUiState()
), TilawahInteractionListener {

    init {
        updateDefaultReciter()
    }

    private fun updateDefaultReciter() {
        tryToExecute(
            execute = { quranRepository.getDefaultReciter() },
            onSuccess = { reciterId -> reciterId?.let { updateRecitersState(it) } },
            onError = ::handleError
        )
    }

    override fun onBackClick() = sendEffect(TilawahEffect.NavigateBack)
    override fun onSearchClick() = sendEffect(TilawahEffect.NavigateToSearch)
    override fun onSelectReciterClick(reciterId: Int) {
        tryToExecute(
            execute = { quranRepository.saveDefaultReciter(reciterId) },
            onSuccess = { updateRecitersState(reciterId) },
            onError = ::handleError
        )
    }

    private fun updateRecitersState(reciterId: Int) {
        updateState { state ->
            state.copy(
                reciters = state.reciters.map {
                    it.copy(isDefault = (it.id == reciterId))
                }
            )
        }
    }

    private fun handleError(errorState: ErrorState) {
        println("Error: $errorState")
    }
}
