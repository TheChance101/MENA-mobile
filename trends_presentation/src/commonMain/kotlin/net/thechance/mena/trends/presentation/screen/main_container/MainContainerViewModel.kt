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
    }

    fun navigateToCategories() {
        if (state.value.isCategoriesAlreadySelectedByUser == true) {
            sendEffect(MainContainerEffect.NavigateToTrends)
        } else {
            sendEffect(MainContainerEffect.NavigateToCategoryPick)
        }
    }

    fun navigateToManageTrends() {
        sendEffect(MainContainerEffect.NavigateToManageTrends)
    }

    fun navigateToUploadReel() {
        sendEffect(MainContainerEffect.NavigateToUploadReel)
    }

    fun navigateToUpdateCategories() {
        sendEffect(MainContainerEffect.NavigateToUpdateCategories)
    }
}