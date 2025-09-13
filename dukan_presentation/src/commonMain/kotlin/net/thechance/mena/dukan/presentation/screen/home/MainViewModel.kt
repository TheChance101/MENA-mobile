package net.thechance.mena.dukan.presentation.screen.home

import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.base.BaseViewModel

class MainViewModel(
    private val dukanRepository: DukanRepository
) : BaseViewModel<MainScreenUiState, MainEffect>(MainScreenUiState()), MainInteractionListener {

    init {
        getDukanState()
    }

    private fun getDukanState() {
        tryToExecute(
            block = dukanRepository::isUserHasDukan,
            onSuccess = ::onGetDukanStateSuccess,
        )
    }

    private fun onGetDukanStateSuccess(isDukanExist: Boolean) {
        updateState {
            copy(isUserHasDukan = isDukanExist)
        }
    }

    override fun onAddDukanIconClicked() {
        // TODO(reason = "use effect to implement navigation")
    }
}