package net.thechance.mena.trends.presentation.screen.category_pick

import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.model.toggleCategory
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class CategoryPickViewModel(
    @Provided private val repository: CategoryRepository
) : BaseViewModel<CategoryPickScreenState, CategoryPickScreenEffect>(
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
            onEnd = ::endLoading
        )
    }

    private fun handleLoadCategoriesSuccess(categories: List<Category>) {
        updateState { copy(categories = categories.toUiStates()) }
    }

    override fun onCategoryClick(categoryId: String) = updateState {
        copy(categories = categories.toggleCategory(categoryId))
    }

    override fun onNextClick() {
        tryToExecute(
            block = { saveSelectedCategories() },
            onSuccess = { sendEffect(CategoryPickScreenEffect.NavigateToTrends) },
            onStart = ::startSaving,
            onEnd = ::endSaving,
            onError = { errorState -> updateState { copy(error = errorState) } },
        )
    }

    private suspend fun saveSelectedCategories() {
        val selectedIds = state.value.categories
            .filter { it.isSelected }
            .mapNotNull { it.value.id }
        repository.updateUserInterestedCategories(selectedIds)
    }

    override fun onBackClick() = sendEffect(CategoryPickScreenEffect.NavigateBack)

    private fun startLoading() = updateState { copy(isLoading = true) }
    private fun endLoading() = updateState { copy(isLoading = false) }

    private fun startSaving() = updateState { copy(isNextButtonLoading = true) }
    private fun endSaving() = updateState { copy(isNextButtonLoading = false) }
}
