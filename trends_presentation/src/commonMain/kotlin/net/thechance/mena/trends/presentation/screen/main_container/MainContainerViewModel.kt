package net.thechance.mena.trends.presentation.screen.main_container

import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class MainContainerViewModel(
    @Provided private val repository: CategoryRepository
): BaseViewModel<MainContainerState, MainContainerEffect>(MainContainerState()) {

    init {
        getUserCategoryStatus()
    }

    private fun getUserCategoryStatus() {
        tryToExecute(
            block = { repository.isCategoriesAlreadySelectedByUser() },
            onSuccess = ::handleGetIsUserCategorySet,
            onError = { errorState -> updateState { copy(error = errorState, isCategoriesAlreadySelectedByUser = false) } },
        )
    }

    fun handleGetIsUserCategorySet(isUserCategorySet: Boolean){
        updateState { copy(isCategoriesAlreadySelectedByUser = isUserCategorySet) }
    }

    fun navigateToCategories(){
        if (state.value.isCategoriesAlreadySelectedByUser == true){
            sendEffect(MainContainerEffect.NavigateToTrends)
        } else {
            sendEffect(MainContainerEffect.NavigateToCategoryPick)
        }
    }

    fun navigateToManageTrends(){
        sendEffect(MainContainerEffect.NavigateToManageTrends)
    }
}