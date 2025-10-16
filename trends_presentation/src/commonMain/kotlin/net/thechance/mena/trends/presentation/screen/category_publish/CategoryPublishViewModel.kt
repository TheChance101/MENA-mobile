package net.thechance.mena.trends.presentation.screen.category_publish

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.screen.category_publish.args.CategoryPublishArgs
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.mapper.toUiStates
import net.thechance.mena.trends.presentation.shared.model.toggleCategory
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class CategoryPublishViewModel(
    @Provided private val categoryPublishArgs: CategoryPublishArgs,
    @Provided private val categoryRepository: CategoryRepository,
    @Provided private val reelsRepository: ReelsRepository,
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
        updateState { copy(categories = categories.toUiStates()) }
    }

    override fun onBackClick() = sendEffect(CategoryPublishEffect.NavigateBack)

    override fun onCategoryClick(categoryId: String) = updateState {
        copy(categories = categories.toggleCategory(categoryId))
    }

    override fun onPublishClick() {
        tryToExecute(
            block = { updateReel() },
            onSuccess = { sendEffect(CategoryPublishEffect.NavigateToTrends) },
            onError = { errorState -> updateState { copy(error = errorState) } },
            onStart = { updateState { copy(isPublishButtonLoadingVisible = true) } },
            onEnd = { updateState { copy(isPublishButtonLoadingVisible = false) } },
            dispatcher = defaultDispatcher
        )
    }

    private suspend fun updateReel() {
        val selectedIds =  state.value.categories
            .filter { it.isSelected }
            .mapNotNull { it.value.id }

        if (selectedIds.isNotEmpty()) {
            reelsRepository.updateReelById(
                id = categoryPublishArgs.trendId,
                description = categoryPublishArgs.description,
                categoryIds = selectedIds
            )
        }
    }
}