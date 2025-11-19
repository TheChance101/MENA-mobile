package net.thechance.mena.trends.presentation.screen.update_categories

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
internal class UpdateCategoriesViewModel(
    @Provided private val repository: CategoryRepository,
    @Provided private val settingsRepository: SettingsRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UpdateCategoriesScreenState, UpdateCategoriesScreenEffect>(
    initialState = UpdateCategoriesScreenState()
), UpdateCategoriesInteractionListener {

    init {
        getCategories()
        getCurrentTheme()
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

    private fun getCategories() {
        tryToExecute(
            block = { repository.getAllCategories() },
            onSuccess = ::handleLoadCategoriesSuccess,
            onError = { errorState -> updateState { copy(errorState = errorState) } },
            onStart = { updateState { copy(isLoading = true) } },
            onEnd = { updateState { copy(isLoading = false) } },
            dispatcher = defaultDispatcher
        )
    }

    override fun onClickCategory(categoryId: String) = updateState {
        copy(categories = categories.toggleCategory(categoryId))
    }

    override fun onClickBack() = sendEffect(UpdateCategoriesScreenEffect.NavigateBack)

    override fun onClickSave() {
        tryToExecute(
            block = { saveSelectedCategories() },
            onSuccess = { sendEffect(UpdateCategoriesScreenEffect.NavigateToTrendsAndShowSuccess) },
            onStart = { updateState { copy(isSaveButtonLoading = true) } },
            onEnd = { updateState { copy(isSaveButtonLoading = false) } },
            onError = { errorState ->
                updateState { copy(errorState = errorState) }
                sendEffect(UpdateCategoriesScreenEffect.SaveFailure)
            },
            dispatcher = defaultDispatcher
        )
    }

    private suspend fun saveSelectedCategories() {
        val originalSelectedIds = state.value.initialCategories
            .filter { it.isSelected }.mapNotNull { it.value.id }

        val currentSelectedIds = state.value.categories
            .filter { it.isSelected }.mapNotNull { it.value.id }

        repository.updateUserCategories(originalSelectedIds, currentSelectedIds)
    }

    private fun handleLoadCategoriesSuccess(categories: List<Category>) {
        updateState {
            copy(
                initialCategories = categories.toUserCategoryUiState(),
                categories = categories.toUserCategoryUiState()
            )
        }
    }

    override fun onClickRetry() {
        updateState{ copy(errorState = null) }
        getCategories()
    }
}
