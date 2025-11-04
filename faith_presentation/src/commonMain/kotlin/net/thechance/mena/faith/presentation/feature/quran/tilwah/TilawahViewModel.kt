package net.thechance.mena.faith.presentation.feature.quran.tilwah

import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel

class TilawahViewModel(
    private val quranRepository: QuranRepository,
) : BaseViewModel<TilawahUiState, TilawahEffect>(
    TilawahUiState()
), TilawahInteractionListener {
    override fun onBackClick() = sendEffect(TilawahEffect.NavigateBack)
    override fun onSearchClick() = sendEffect(TilawahEffect.NavigateToSearch)
    override fun onSelectReciterClick(reciterId: Int) {
        tryToExecute(
            execute = { quranRepository.saveDefaultReciter(reciterId) },
            onError = { println("Error: $it") }
        )
    }
}
