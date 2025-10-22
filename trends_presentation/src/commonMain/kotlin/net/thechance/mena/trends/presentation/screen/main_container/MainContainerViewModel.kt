package net.thechance.mena.trends.presentation.screen.main_container

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.trends.domain.exception.NoInternetException
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class MainContainerViewModel(
    @Provided private val repository: CategoryRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<MainContainerState, MainContainerEffect, MainContainerErrorState>(MainContainerState()) {

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
            dispatcher = defaultDispatcher,
            errorMapper = ::mapError
        )
    }

    fun onUserCategoryStatusReceived(isUserCategorySet: Boolean) {
        updateState { copy(isCategoriesAlreadySelectedByUser = isUserCategorySet) }
        navigateBasedOnCategoryState(isUserCategorySet)
    }

    private fun navigateBasedOnCategoryState(hasUserSelectedCategories: Boolean) {
        if (hasUserSelectedCategories) {
            sendEffect(MainContainerEffect.NavigateToReelHome)
        } else {
            sendEffect(MainContainerEffect.NavigateToCategoryPick)
        }
    }

    private fun mapError(throwable: Throwable): MainContainerErrorState {
        return when (throwable) {
            is NoInternetException -> MainContainerErrorState.NoInternet
            else -> MainContainerErrorState.Unknown(message = throwable.message).also { logError(throwable)}
        }.also { errorState ->
            Logger.e(TAG) { errorState.toString() }
        }
    }

    private companion object{
        const val TAG ="MainContainerErrorState"
    }
}