package net.thechance.mena.dukan.presentation.screen.CreateDukan.pendingDukanScreen

import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.screen.createDukan.pendingDukanScreen.PendingDukanEffect
import net.thechance.mena.dukan.presentation.screen.createDukan.pendingDukanScreen.PendingDukanUiState

class PendingDukanViewModel(
    private val dukanRepository: DukanRepository
) :
    BaseViewModel<PendingDukanUiState, PendingDukanEffect>(PendingDukanUiState()) {

    init {
        initializeData()
    }

    private fun initializeData() {
        tryToExecute(
            block = { dukanRepository.getMyDukan() },
            onSuccess = { dukan -> updateState { copy(brandName = dukan.name) } },
        )
    }

    fun onNavigateBack() = emitEffect(PendingDukanEffect.NavigateBack)
}