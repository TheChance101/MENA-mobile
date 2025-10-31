package net.thechance.mena.dukan.presentation.viewModel.dukanCategories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class DukanCategoriesViewModel(
    private val dukanManagementRepository: DukanManagementRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<DukanCategoriesUiState, DukanCategoriesEffects>(
    initialState = DukanCategoriesUiState(),
    defaultDispatcher = defaultDispatcher
), DukanCategoriesInteractionListener {

    init {
        getCategories()
    }

    private fun getCategories() {
        tryToExecute(
            onStart = ::onGetCategoriesStart,
            block = ::getCategoriesBlock,
            onSuccess = ::onGetCategoriesSuccess,
            onError = ::onGetCategoriesError
        )
    }

    private fun onGetCategoriesStart() {
        updateState {
            copy(
                dukanCategoriesState = DukanCategoriesUiState.DukanCategoriesState.LOADING
            )
        }
    }

    private suspend fun getCategoriesBlock(): List<DukanCategoriesUiState.CategoryUiState> {
        return dukanManagementRepository.getCategories().map { it.toUiState() }
    }

    private fun onGetCategoriesSuccess(categories: List<DukanCategoriesUiState.CategoryUiState>) {
        updateState {
            copy(
                categories = categories,
                dukanCategoriesState = DukanCategoriesUiState.DukanCategoriesState.LOADED
            )
        }
    }

    private fun onGetCategoriesError(error: Throwable) {
        updateState { copy(dukanCategoriesState = DukanCategoriesUiState.DukanCategoriesState.ERROR) }
        if (error != NoInternetException()) {
            showSnackBar(
                message = Res.string.something_went_wrong,
                type = SnackBarType.ERROR
            )
        }
    }


    override fun onBackClicked() {
        emitEffect(effect = DukanCategoriesEffects.NavigateBack)
    }

    override fun onCategoryClicked(
        categoryName: String,
        categoryId: String
    ) {
        emitEffect(
            effect = DukanCategoriesEffects.NavigateToDukansOfCategory(
                categoryName = categoryName,
                categoryId = categoryId
            )
        )
    }

    override fun onDismissSnackBar() {
        updateState { copy(snackBarUiState = null) }
    }

    override fun onRetryClicked() {
        getCategories()
    }

    private fun showSnackBar(message: StringResource, type: SnackBarType) {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    message = message,
                    snackBarType = type
                )
            )
        }
    }
}