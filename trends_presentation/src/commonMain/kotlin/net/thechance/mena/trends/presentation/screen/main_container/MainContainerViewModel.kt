package net.thechance.mena.trends.presentation.screen.main_container

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.collectLatest
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class MainContainerViewModel(
    @Provided private val repository: CategoryRepository,
    @Provided private val settingsRepository: SettingsRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<MainContainerState, MainContainerEffect>(MainContainerState()), MainContainerInteractionListener {

    init {
        checkIfUserSelectedCategories()
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
            sendEffect(MainContainerEffect.NavigateToReelHome)
        } else {
            sendEffect(MainContainerEffect.NavigateToCategoryPick)
        }
    }

    override fun onClickRetry() {
        updateState { copy(error = null) }
        checkIfUserSelectedCategories()
    }
}