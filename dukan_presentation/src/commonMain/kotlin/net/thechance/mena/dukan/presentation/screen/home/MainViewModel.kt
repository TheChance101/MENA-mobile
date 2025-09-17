package net.thechance.mena.dukan.presentation.screen.home

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.base.BaseViewModel

class MainViewModel(
    private val dukanRepository: DukanRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<MainScreenUiState, MainEffect>(initialState = MainScreenUiState(), defaultDispatcher = dispatcher), MainInteractionListener {

    init {
        getDukanState()
    }

    private fun getDukanState() {
        tryToExecute(
            block = ::getDukanStateBlock,
            onSuccess = ::onGetDukanStateSuccess,
            onError = ::onGetDukanStateError
        )
    }

    private suspend fun getDukanStateBlock(): Dukan.Status {
        return dukanRepository.isUserHasDukan().let { isDukanExist ->
            if (isDukanExist) dukanRepository.getMyDukan().status
            else Dukan.Status.None
        }
    }

    private fun onGetDukanStateSuccess(dukanStatus: Dukan.Status) {
        updateState {
            copy(dukanStatus = dukanStatus.toUiState())
        }
    }

    private fun onGetDukanStateError(error: Throwable) {
        updateState {
            copy(errorMessage = error.message)
        }
    }

    override fun onDukanButtonClicked() {
        when (state.value.dukanStatus) {
            MainScreenUiState.DukanStatusUi.None -> emitEffect(MainEffect.NavigateToAddDukanScreen)
            MainScreenUiState.DukanStatusUi.Pending -> emitEffect(MainEffect.NavigateToPendingDukanScreen)
        }
    }
}