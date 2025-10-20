package net.thechance.mena.dukan.presentation.viewModel.dukanCategories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.no_internet_message
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class DukanCategoriesViewModel(
    private val dukanRepository: DukanRepository,
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
            block = ::getCategoriesBlock,
            onSuccess = ::onGetCategoriesSuccess,
            onError = ::onGetCategoriesError
        )
    }

    private suspend fun getCategoriesBlock(): List<DukanCategoriesUiState.CategoryUiState> {
        return dukanRepository.getCategories().map { it.toUiState() }
    }

    private fun onGetCategoriesSuccess(categories: List<DukanCategoriesUiState.CategoryUiState>) {
        updateState { copy(categories = categories) }
    }

    private fun onGetCategoriesError(error: Throwable) {
        val messageRes = when (error) {
            is NoInternetException -> Res.string.no_internet_message
            else -> Res.string.something_went_wrong
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
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