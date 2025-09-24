package net.thechance.mena.trends.presentation.shared.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.trends.domain.exception.NoInternetException
import net.thechance.mena.trends.domain.util.Logger
import net.thechance.mena.trends.presentation.shared.util.throttleFirst
import org.koin.mp.KoinPlatform.getKoin

internal abstract class BaseViewModel<State, Effect>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    val effect = _effect.throttleFirst(THROTTLE_WINDOW_DURATION)

    private val logger: Logger = getKoin().get()

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
        onError: (ErrorState) -> Unit,
        onStart: () -> Unit = {},
        onEnd: () -> Unit = {},
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        scope: CoroutineScope = viewModelScope,
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            onError(ErrorState.RequestFailed(exception.message))
        }

        return scope.launch(dispatcher + exceptionHandler) {
            onStart()

            runCatching { block() }
                .onSuccess { onSuccess(it) }
                .onFailure {
                    mapExceptionToErrorState(
                        throwable = it,
                        onError = onError
                    )
                }
            onEnd()
        }
    }

    private suspend fun mapExceptionToErrorState(
        throwable: Throwable,
        onError: suspend (ErrorState) -> Unit,
    ) {
        logError(throwable)
        val message = throwable.message
        when (throwable) {
            is NoInternetException -> ErrorState.NoInternet
            else -> ErrorState.RequestFailed(message).also { logError(throwable) }
        }.also { errorState ->
            logger.logError(LOG_TAG, "error state: $errorState")
        }.let { onError(it) }
    }

    private fun logError(throwable: Throwable) {
        logger.logError(LOG_TAG, "${throwable}: ${throwable.message}")
    }

    companion object {
        private const val THROTTLE_WINDOW_DURATION = 300L
        private const val LOG_TAG = "BaseViewModel"
    }
}