package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState.DukanStatusUi

class MainViewModel(
    private val dukanRepository: DukanRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<MainScreenUiState, MainEffect>(
    initialState = MainScreenUiState(),
    defaultDispatcher = dispatcher
), MainInteractionListener {

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

    private suspend fun getDukanStateBlock(): MainScreenUiState.DukanState? {
        return dukanRepository.getMyDukanStatus()?.toUiState()
    }

    private fun onGetDukanStateSuccess(dukanState: MainScreenUiState.DukanState?) {
        when (dukanState) {
            null -> updateState {
                copy(
                    errorMessage = "Dukan not found",
                    dukanState = MainScreenUiState.DukanState(
                        name = "",
                        status = DukanStatusUi.None
                    )
                )
            }

            else -> updateState { copy(dukanState = dukanState) }
        }
    }

    private fun onGetDukanStateError(error: Throwable) {
        updateState {
            copy(errorMessage = error.message)
        }
    }

    override fun onDukanButtonClicked() {
        when (state.value.dukanState.status) {
            DukanStatusUi.None -> emitEffect(MainEffect.NavigateToAddDukanScreen)
            DukanStatusUi.Pending -> emitEffect(MainEffect.NavigateToPendingDukanScreen)
        }
    }
}