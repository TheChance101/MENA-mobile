package net.thechance.mena.trends.presentation.screen.update_interests

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.mapper.toUiStates
import net.thechance.mena.trends.presentation.shared.model.toggleCategory
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided


@KoinViewModel
internal class UpdateInterestsViewModel(
    @Provided private val repository: CategoryRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UpdateInterestsScreenState, UpdateInterestsScreenEffect>(
    initialState = UpdateInterestsScreenState()
), UpdateInterestsInteractionListener {

    init {
        initializeCategories()
    }

    private fun initializeCategories() {
        tryToExecute(
            block = { repository.getAllCategories() },
            onSuccess = ::handleLoadCategoriesSuccess,
            onError = { errorState -> updateState { copy(error = errorState) } },
            onStart = ::startLoading,
            onEnd = ::endLoading,
            dispatcher = defaultDispatcher
        )
    }

    private fun handleLoadCategoriesSuccess(categories: List<Category>) {
        updateState { copy(categories = categories.toUiStates()) }
    }

    override fun onCategoryClick(categoryId: String) = updateState {
        copy(categories = categories.toggleCategory(categoryId))
    }

    override fun onSaveClick() {
        tryToExecute(
            block = { saveSelectedCategories() },
            onSuccess = { sendEffect(UpdateInterestsScreenEffect.NavigateToTrends) },
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
        repository.updateUserCategories(selectedIds)
    }

    override fun onBackClick() = sendEffect(UpdateInterestsScreenEffect.NavigateBack)

    private fun startLoading() = updateState { copy(isLoading = true) }
    private fun endLoading() = updateState { copy(isLoading = false) }
    private fun startSaving() = updateState { copy(isNextButtonLoading = true) }
    private fun endSaving() = updateState { copy(isNextButtonLoading = false) }
}
