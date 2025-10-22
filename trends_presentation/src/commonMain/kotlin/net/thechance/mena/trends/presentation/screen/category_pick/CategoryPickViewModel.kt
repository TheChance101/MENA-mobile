package net.thechance.mena.trends.presentation.screen.category_pick

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.exception.NoInternetException
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.model.mapper.toUserCategoryUiState
import net.thechance.mena.trends.presentation.shared.model.toggleCategory
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class CategoryPickViewModel(
    @Provided private val repository: CategoryRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<CategoryPickScreenState, CategoryPickScreenEffect, CategoryPickErrorState>(
    initialState = CategoryPickScreenState()
), CategoryPickInteractionListener {

    init {
        loadCategories()
    }

    private fun loadCategories() {
        tryToExecute(
            block = { repository.getAllCategories() },
            onSuccess = ::handleLoadCategoriesSuccess,
            onError = { errorState -> updateState { copy(error = errorState) } },
            onStart = ::startLoading,
            onEnd = ::endLoading,
            dispatcher = defaultDispatcher,
            errorMapper = ::mapError
        )
    }

    private fun handleLoadCategoriesSuccess(categories: List<Category>) {
        updateState { copy(categories = categories.toUserCategoryUiState()) }
    }

    override fun onCategoryClick(categoryId: String) = updateState {
        copy(categories = categories.toggleCategory(categoryId))
    }

    override fun onNextClick() {
        tryToExecute(
            block = { saveSelectedCategories() },
            onSuccess = { sendEffect(CategoryPickScreenEffect.NavigateToHome) },
            onStart = ::startSaving,
            onEnd = ::endSaving,
            onError = { errorState -> updateState { copy(error = errorState) } },
            dispatcher = defaultDispatcher,
            errorMapper = ::mapError
        )
    }

    private suspend fun saveSelectedCategories() {
        val selectedIds = state.value.categories
            .filter { it.isSelected }
            .mapNotNull { it.value.id }
        repository.initializeUserCategories(selectedIds)
    }

    private fun mapError(throwable: Throwable): CategoryPickErrorState {
        return when (throwable) {
            is NoInternetException -> CategoryPickErrorState.NoInternet
            else -> CategoryPickErrorState.RequestFailed(throwable.message).also { logError(throwable) }
        }.also { errorState -> Logger.e(TAG) { errorState.toString() } }
    }

    override fun onBackClick() = sendEffect(CategoryPickScreenEffect.NavigateBack)

    private fun startLoading() = updateState { copy(isLoading = true) }
    private fun endLoading() = updateState { copy(isLoading = false) }

    private fun startSaving() = updateState { copy(isNextButtonLoading = true) }
    private fun endSaving() = updateState { copy(isNextButtonLoading = false) }

    private companion object{
        const val TAG ="CategoryPickErrorState"
    }
}
