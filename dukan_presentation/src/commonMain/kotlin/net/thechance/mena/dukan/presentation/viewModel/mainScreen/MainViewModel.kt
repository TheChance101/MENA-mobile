package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.domain.exceptions.DukanNotFoundException
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.toUiState
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
        getCategories()
    }

    private fun getCategories() {
        tryToExecute(
            onStart = { updateState { copy(dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.Loading)) } },
            block = ::getCategoriesBlock,
            onSuccess = ::onGetCategoriesSuccess,
            onError = ::onGetCategoriesError
        )
    }

    private fun onGetCategoriesError(error: Throwable) {
        updateState {
            copy(
                errorMessage = error.message
            )
        }
    }

    private suspend fun getCategoriesBlock(): List<DukanCategoryUiState> {
        return dukanRepository.getCategories().toUiState()
    }

    private fun onGetCategoriesSuccess(categoryUiState: List<DukanCategoryUiState>) {
        updateState {
            copy(
                categories = categoryUiState
            )
        }
    }

    private fun getDukanState() {
        tryToExecute(
            onStart = { updateState { copy(dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.Loading)) } },
            block = ::getDukanStateBlock,
            onSuccess = ::onGetDukanStateSuccess,
            onError = ::onGetDukanStateError
        )
    }

    private suspend fun getDukanStateBlock(): MainScreenUiState.DukanState? {
        return dukanRepository.getMyDukanStatus()?.toUiState()
    }

    private fun onGetDukanStateSuccess(dukanState: MainScreenUiState.DukanState?) {
        if (dukanState == null) {
            updateState { copy(dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.None)) }
        } else {
            updateState { copy(dukanState = dukanState) }
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
            DukanStatusUi.None -> emitEffect(MainEffect.NavigateToAddDukanScreen)
            DukanStatusUi.Pending -> emitEffect(MainEffect.NavigateToPendingDukanScreen)
            DukanStatusUi.Approved -> emitEffect(MainEffect.NavigateToManageDukanScreen)
            DukanStatusUi.Loading -> {}
        }
    }

    override fun onViewMoreButtonClick() {
        emitEffect(MainEffect.NavigateCategoryToScreen)
    }

    override fun onCategorySelectedClick(categoryId: String) {
        emitEffect(MainEffect.NavigateToDukansScreenByCategory(categoryId))
    }

    override fun onNearestDukanClick(dukanId: String) {
        emitEffect(MainEffect.NavigateSelectedNearsetDukan(dukanId))
    }

    override fun onEditorPickDukanClick(dukanId: String) {
        emitEffect(MainEffect.NavigateSelectedEditorPickDukan(dukanId))
    }
}