package net.thechance.mena.faith.presentation.feature.quran.tilwah

import net.thechance.mena.faith.presentation.base.BaseViewModel

class TilawahViewModel : BaseViewModel<TilawahUiState, TilawahEffect>(
    TilawahUiState()
), TilawahInteractionListener {
    override fun onBackClick() = sendEffect(TilawahEffect.NavigateBack)
    override fun onSearchClick() = sendEffect(TilawahEffect.NavigateToSearch)
    override fun onSelectReciterClick(reciterId: Int) {
        //TODO() not implemented yet
    }

}