package net.thechance.mena.trends.presentation.screen.category_pick

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.collectLatest
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.model.mapper.toUserCategoryUiState
import net.thechance.mena.trends.presentation.shared.model.toggleCategory
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class CategoryPickViewModel(
    @Provided private val repository: CategoryRepository,
    @Provided private val settingsRepository: SettingsRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<CategoryPickScreenState, CategoryPickScreenEffect>(
    initialState = CategoryPickScreenState()
), CategoryPickInteractionListener {

    init {
        loadCategories()
        getCurrentTheme()
    }

    private fun loadCategories() {
        tryToExecute(
            block = { repository.getAllCategories() },
            onSuccess = ::handleLoadCategoriesSuccess,
            onError = { errorState -> updateState { copy(error = errorState) } },
            onStart = ::startLoading,
            onEnd = ::endLoading,
            dispatcher = defaultDispatcher
        )
    }

    fun getCurrentTheme() {
        tryToExecute(
            block = {
                settingsRepository.observeAppTheme().collectLatest { theme ->
                    updateState {
                        copy(currentTheme = theme)
                    }
                }
            }
        )
    }

    private fun handleLoadCategoriesSuccess(categories: List<Category>) {
        updateState { copy(categories = categories.toUserCategoryUiState()) }
    }

    override fun onClickCategory(categoryId: String) = updateState {
        copy(categories = categories.toggleCategory(categoryId))
    }

    override fun onClickNext() {
        tryToExecute(
            block = { saveSelectedCategories() },
            onSuccess = { sendEffect(CategoryPickScreenEffect.NavigateToHome) },
            onStart = ::startSaving,
            onEnd = ::endSaving,
            onError = { errorState -> updateState { copy(error = errorState) } },
            dispatcher = defaultDispatcher
        )
    }

    private suspend fun saveSelectedCategories() {
        val selectedIds = state.value.categories
            .filter { it.isSelected }
            .mapNotNull { it.value.id }
        repository.initializeUserCategories(selectedIds)
    }

    override fun onClickBack() = sendEffect(CategoryPickScreenEffect.NavigateBack)

    override fun onClickRetry() {
        updateState { copy(error = null) }
        loadCategories()
    }
    private fun startLoading() = updateState { copy(isLoading = true) }

    private fun endLoading() = updateState { copy(isLoading = false) }

    private fun startSaving() = updateState { copy(isNextButtonLoading = true) }

    private fun endSaving() = updateState { copy(isNextButtonLoading = false) }

}
