package net.thechance.mena.trends.presentation.shared.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.trends.presentation.shared.util.throttleFirst

abstract class BaseViewModel<State, Effect>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    val effect = _effect.throttleFirst(300L)

    protected fun updateState(updater: State.() -> State) {
        _state.update { updater(it) }
    }

    protected fun sendEffect(
        effect: Effect,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            _effect.emit(effect)
        }
    }

    protected fun <R> tryToExecute(
        block: suspend () -> R,
        onSuccess: (R) -> Unit,
        onError: (Throwable) -> Unit,
        onStart: () -> Unit = {},
        onEnd: () -> Unit = {},
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        scope: CoroutineScope = viewModelScope
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            onError(exception)
        }

        return scope.launch(dispatcher + exceptionHandler) {
            onStart()
            runCatching { block() }
                .onSuccess { onSuccess(it) }
                .onFailure { onError(it) }
            onEnd()
        }
    }
}