package net.thechance.mena.trends.presentation.shared.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.trends.presentation.shared.util.throttleFirst
import kotlin.coroutines.cancellation.CancellationException

internal abstract class BaseViewModel<State, Effect, Error>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    val effect = _effect.throttleFirst(THROTTLE_WINDOW_DURATION)

    protected fun updateState(updater: State.() -> State) {
        _state.update { updater(it) }
    }

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch(Dispatchers.Main) {
            _effect.emit(effect)
        }
    }

    protected fun <R> tryToExecute(
        block: suspend () -> R,
        onSuccess: (R) -> Unit = {},
        onError: (Error) -> Unit = {},
        onStart: () -> Unit = {},
        onEnd: () -> Unit = {},
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        scope: CoroutineScope = viewModelScope,
        errorMapper: (Throwable) -> Error
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            onError(errorMapper(exception))
        }

        return scope.launch(dispatcher + exceptionHandler) {
            onStart()

            runCatching { block() }
                .onSuccess { onSuccess(it) }
                .onFailure { onError(errorMapper(it)) }
            onEnd()
        }
    }

    protected fun <R> tryToCollectFlow(
        block: () -> Flow<R>,
        onStart: () -> Unit = {},
        onNewValue: (R) -> Unit,
        onError: (Error) -> Unit,
        onEnd: () -> Unit = {},
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        scope: CoroutineScope = viewModelScope,
        errorMapper: (Throwable) -> Error
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            if (exception is CancellationException) return@CoroutineExceptionHandler
            onError(errorMapper(exception))
        }

        return scope.launch(dispatcher + exceptionHandler) {
            block()
                .flowOn(dispatcher)
                .onStart { onStart() }
                .onEach { onNewValue(it) }
                .onCompletion { throwable ->
                    throwable?.let {
                        if (it is CancellationException) return@onCompletion
                        onError(errorMapper(it))
                    } ?: onEnd()
                }
                .catch { throwable -> onError(errorMapper(throwable)) }
                .collect()
        }
    }

    fun logError(throwable: Throwable) {
        Logger.e(LOG_TAG){"${throwable}: ${throwable.message}"}
    }

    companion object {
        private const val THROTTLE_WINDOW_DURATION = 300L
        private const val LOG_TAG = "BaseViewModel"
    }
}