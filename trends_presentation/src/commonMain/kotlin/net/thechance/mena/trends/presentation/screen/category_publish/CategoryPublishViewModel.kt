package net.thechance.mena.trends.presentation.screen.category_publish

import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.presentation.screen.category_pick.toUiStates
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.model.toggleCategory
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class CategoryPublishViewModel(
    @Provided private val categoryRepository: CategoryRepository,
) : BaseViewModel<CategoryPublishState, CategoryPublishEffect>(
    initialState = CategoryPublishState()
), CategoryPublishInteractionListener {

    init {
        loadCategories()
    }

    private fun loadCategories() {
        tryToExecute(
            block = { categoryRepository.getAllCategories() },
            onSuccess = ::handleLoadCategoriesSuccess,
            onError = { errorState -> updateState { copy(error = errorState) } },
            onStart = ::startLoading,
            onEnd = ::endLoading
        )
    }

    private fun handleLoadCategoriesSuccess(categories: List<Category>) {
        updateState { copy(categories = categories.toUiStates()) }
    }

    private fun startLoading() = updateState { copy(isLoading = true) }

    private fun endLoading() = updateState { copy(isLoading = false) }

    override fun onBackClick() = sendEffect(CategoryPublishEffect.NavigateBack)

    override fun onCategoryClick(categoryId: String) = updateState {
        copy(categories = categories.toggleCategory(categoryId))
    }

    override fun onPublishClick() {
        tryToExecute(
            block = { saveSelectedCategories() },
            onSuccess = { sendEffect(CategoryPublishEffect.NavigateToTrends) },
            onStart = ::onStartPublish,
            onEnd = ::onEndPublish,
            onError = { errorState -> updateState { copy(error = errorState) } },
        )
    }

    private suspend fun saveSelectedCategories() {
        val selectedIds = state.value.categories
            .filter { it.isSelected }
            .mapNotNull { it.value.id }

        if (selectedIds.isNotEmpty()) {
            categoryRepository.updateUserInterestedCategories(selectedIds)
        }
    }

    private fun onStartPublish() = updateState { copy(isPublishButtonVisible = true) }
    private fun onEndPublish() = updateState { copy(isPublishButtonVisible = false) }
}