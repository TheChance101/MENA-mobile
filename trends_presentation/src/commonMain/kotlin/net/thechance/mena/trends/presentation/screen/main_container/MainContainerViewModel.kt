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
) : BaseViewModel<MainContainerState, MainContainerEffect>(MainContainerState()) {

    init {
        getUserCategoryStatus()
    }

    private fun getUserCategoryStatus() {
        tryToExecute(
            block = { repository.isCategoriesAlreadySelectedByUser() },
            onSuccess = ::handleGetIsUserCategorySet,
            onError = { errorState ->
                updateState { copy(error = errorState, isCategoriesAlreadySelectedByUser = false) }
            },
            dispatcher = defaultDispatcher
        )
    }

    fun handleGetIsUserCategorySet(isUserCategorySet: Boolean) {
        updateState { copy(isCategoriesAlreadySelectedByUser = isUserCategorySet) }
        navigateBasedOnCategoryState(isUserCategorySet)
    }

    private fun navigateBasedOnCategoryState(isUserCategorySet: Boolean) {
        if (isUserCategorySet) {
            sendEffect(MainContainerEffect.NavigateToReelHome)
        } else {
            sendEffect(MainContainerEffect.NavigateToCategoryPick)
        }
    }
}