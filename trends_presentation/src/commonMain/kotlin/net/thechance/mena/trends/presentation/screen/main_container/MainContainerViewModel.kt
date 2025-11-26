package net.thechance.mena.trends.presentation.screen.main_container

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class MainContainerViewModel(
    @Provided private val repository: CategoryRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<MainContainerState, MainContainerEffect>(MainContainerState()), MainContainerInteractionListener {

    init {
        checkIfUserSelectedCategories()
    }

    private fun checkIfUserSelectedCategories() {
        tryToExecute(
            block = { repository.isCategoriesAlreadySelectedByUser() },
            onSuccess = ::onUserCategoryStatusReceived,
            onError = { errorState ->
                updateState { copy(error = errorState, isCategoriesAlreadySelectedByUser = false) }
            },
            dispatcher = defaultDispatcher
        )
    }

    fun onUserCategoryStatusReceived(isUserCategorySet: Boolean) {
        updateState { copy(isCategoriesAlreadySelectedByUser = isUserCategorySet) }
        navigateBasedOnCategoryState(isUserCategorySet)
    }

    private fun navigateBasedOnCategoryState(hasUserSelectedCategories: Boolean) {
        if (hasUserSelectedCategories) {
            sendEffect(MainContainerEffect.NavigateToTrendHome)
        } else {
            sendEffect(MainContainerEffect.NavigateToCategoryPick)
        }
    }

    override fun onClickRetry() {
        updateState { copy(error = null) }
        checkIfUserSelectedCategories()
    }
}