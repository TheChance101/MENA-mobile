package net.thechance.mena.faith.presentation.feature.quran.tilwah

import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel

class TilawahViewModel(val quranRepository: QuranRepository) :
    BaseViewModel<TilawahUiState, TilawahEffect>(
        TilawahUiState()
    ), TilawahInteractionListener {

    init {
        getAllReciters()
    }

    override fun onBackClick() = sendEffect(TilawahEffect.NavigateBack)
    override fun onSearchClick() = sendEffect(TilawahEffect.NavigateToSearch)
    override fun onSelectReciterClick(reciterId: Int) {
        updateState { it.copy(selectedReciterId = reciterId) }
    }

    private fun getAllReciters() {
        tryToExecute(
            execute = { quranRepository.getReciters() },
            onSuccess = { ::getAllRecitersSuccessfully })
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