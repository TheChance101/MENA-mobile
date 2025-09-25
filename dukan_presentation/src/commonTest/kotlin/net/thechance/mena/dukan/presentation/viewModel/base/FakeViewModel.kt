package net.thechance.mena.dukan.presentation.viewModel.base

import androidx.navigation.NavOptions
import dev.mokkery.mock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import net.thechance.mena.dukan.presentation.navigation.DukanNavigator
import net.thechance.mena.dukan.presentation.navigation.DukanRoute

class FakeViewModel(
    initialState: String = "initial",
    navigator: DukanNavigator = mock<DukanNavigator>(),
    dispatcher: CoroutineDispatcher = StandardTestDispatcher()
) : BaseViewModel<String>(initialState, dispatcher,navigator) {

    fun setState(newValue: String) {
        updateState { newValue }
    }

    fun launchSuccess() {
        tryToExecute(
            block = { "success" },
            onSuccess = { result -> updateState { result } }
        )
    }

    fun launchFailure() {
        tryToExecute(
            block = { throw IllegalArgumentException("boom") },
            onError = { updateState { "error:${it.message}" } }
        )
    }


    fun collectFlow(flow: Flow<String>) {
        tryToCollect(
            block = { flow },
            onCollect = { value -> updateState { value } }
        )
    }

    fun navigateToScreen(route: DukanRoute,navOptions: NavOptions? = null) {
        navigate(route = route, navOptions = navOptions)
    }

    fun navigateBack() {
        navigateUp()
    }
}