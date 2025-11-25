package net.thechance.mena.trends.presentation.screen.category_publish

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.domain.repository.TrendsRepository
import net.thechance.mena.trends.presentation.screen.category_publish.args.CategoryPublishArgs
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.model.mapper.toTrendCategoryUiState
import net.thechance.mena.trends.presentation.shared.model.toggleCategory
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class CategoryPublishViewModel(
    @Provided private val categoryPublishArgs: CategoryPublishArgs,
    @Provided private val categoryRepository: CategoryRepository,
    @Provided private val trendsRepository: TrendsRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
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
            onStart = { updateState { copy(isLoading = true) } },
            onEnd = { updateState { copy(isLoading = false) } },
            dispatcher = defaultDispatcher
        )
    }

    private fun handleLoadCategoriesSuccess(categories: List<Category>) {
        updateState { copy(categories = categories.toTrendCategoryUiState()) }
    }

    override fun onClickBack() = sendEffect(CategoryPublishEffect.NavigateBack)

    override fun onClickCategory(categoryId: String) = updateState {
        copy(categories = categories.toggleCategory(categoryId))
    }

    override fun onClickPublish() {
        tryToExecute(
            block = { updateTrend() },
            onSuccess = { sendEffect(CategoryPublishEffect.NavigateToHome) },
            onError = { errorState -> updateState { copy(error = errorState) } },
            onStart = { updateState { copy(isPublishButtonLoadingVisible = true) } },
            onEnd = { updateState { copy(isPublishButtonLoadingVisible = false) } },
            dispatcher = defaultDispatcher
        )
    }

    override fun onClickRetry() {
        updateState { copy(error = null) }
        loadCategories()
    }

    private suspend fun updateTrend() {
        val selectedIds =  state.value.categories
            .filter { it.isSelected }
            .mapNotNull { it.value.id }

        if (selectedIds.isNotEmpty()) {
            trendsRepository.updateTrendById(
                id = categoryPublishArgs.trendId,
                description = categoryPublishArgs.description,
                categoryIds = selectedIds
            )
        }
    }
}