package net.thechance.mena.identity.presentation.core.base

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

abstract class BaseScreenModel<S, E>(initialState: S) : ScreenModel {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<E>()
    val effect = _effect.asSharedFlow().throttleFirst(500).mapNotNull { it }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleUncaughtException(throwable)
    }

    protected fun <T> tryToExecute(
        function: suspend () -> T,
        onSuccess: suspend (T) -> Unit = {},
        onError: (Throwable) -> Unit = {},
        inScope: CoroutineScope = screenModelScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onError(throwable)
        }
        return inScope.launch(exceptionHandler + dispatcher) {
            val result = function()
            onSuccess(result)
        }
    }

    protected fun <T> tryToCollect(
        function: suspend () -> Flow<T?>,
        onNewValue: (T) -> Unit,
        onError: (Throwable) -> Unit = {},
        inScope: CoroutineScope = screenModelScope,
        dispatcher: CoroutineDispatcher,
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable -> onError(throwable) }
        return inScope.launch(exceptionHandler + dispatcher) {
            function()
                .catch { onError(it) }
                .collectLatest { it?.let { onNewValue(it) } }
        }
    }

    protected fun updateState(updater: S.() -> S) {
        _state.update(updater)
    }

    protected fun sendNewEffect(newEffect: E) {
        screenModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                _effect.emit(newEffect)
            } catch (e: Exception) {
                handleUncaughtException(e)
            }
        }
    }

    private fun handleUncaughtException(throwable: Throwable) {
        throwable.printStackTrace()
    }

    @OptIn(ExperimentalTime::class)
    private fun <T> Flow<T>.throttleFirst(periodMillis: Long): Flow<T> {
        require(periodMillis > 0)
        return flow {
            var lastTime = 0L
            collect { value ->
                val currentTime = Clock.System.now().toEpochMilliseconds()
                if (currentTime - lastTime >= periodMillis) {
                    lastTime = currentTime
                    emit(value)
                }
            }
        }
    }
}