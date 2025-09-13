package net.thechance.mena.dukan.presentation.screen.home

import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.base.BaseViewModel

class HomeViewModel(
    private val dukanRepository: DukanRepository
) : BaseViewModel<HomeScreenUiState, HomeEffect>(HomeScreenUiState()), HomeInteractionListener {

    init {
        getDukanButtonState()
    }

    private fun getDukanButtonState() {
        tryToExecute(
            block = { dukanRepository.isUserHasDukan() },
            onSuccess = { isDukanExist ->
                updateState {
                    copy(isPending = isDukanExist)
                }
            },
            onError = {
                updateState {
                    copy(error = it.message)
                }
            },
        )
    }

    override fun onAddDukanButtonClicked() {
        // TODO(reason = "use effect to implement navigation")
    }
}