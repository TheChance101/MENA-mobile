package net.thechance.mena.dukan.presentation.viewModel.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher

class FakeViewModel(
    initialState: String = "initial",
    dispatcher: CoroutineDispatcher = StandardTestDispatcher()
) : BaseViewModel<String, String>(initialState, dispatcher) {

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

    fun sendEffect() {
        emitEffect("effect")
    }

    fun collectFlow(flow: Flow<String>) {
        tryToCollect(
            block = { flow },
            onCollect = { value -> updateState { value } }
        )
    }
}