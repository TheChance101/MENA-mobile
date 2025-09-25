package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.domain.exceptions.DukanNotFoundException
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.DukanNavigator
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState.DukanStatusUi

class MainViewModel(
    private val dukanRepository: DukanRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    navigator: DukanNavigator
) : BaseViewModel<MainScreenUiState>(
    initialState = MainScreenUiState(),
    defaultDispatcher = dispatcher,
    dukanNavigator = navigator
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
        dukanState?.let { state ->
            updateState { copy(dukanState = state) }
        }
    }

    private fun onGetDukanStateError(error: Throwable) {
        when (error) {
            is DukanNotFoundException -> updateState {
                copy(
                    errorMessage = error.message,
                    dukanState = MainScreenUiState.DukanState(
                        status = DukanStatusUi.None
                    )
                )
            }
        }
    }

    override fun onDukanButtonClicked() {
        when (state.value.dukanState.status) {
            DukanStatusUi.None -> navigate(DukanRoute.CreateDukanScreenRoute)
            DukanStatusUi.Pending -> navigate(DukanRoute.PendingScreenRoute(state.value.dukanState.name))
        }
    }
}