package net.thechance.mena.identity.presentation.base

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.exception.AuthenticationException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


abstract class BaseScreenModel<S, E>(initialState: S) : ScreenModel {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<E>()
    val effect = _effect.asSharedFlow().throttleFirst(500).mapNotNull { it }

    protected fun tryToExecute(
        function: suspend () -> Unit,
        onSuccess: () -> Unit,
        onError: (ErrorState) -> Unit,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        inScope: CoroutineScope = screenModelScope,
    ): Job {
        return runWithErrorCheck(onError, inScope, dispatcher) {
            function()
            onSuccess()
        }
    }

    protected fun <T> tryToExecute(
        function: suspend () -> T,
        onSuccess: (T) -> Unit,
        onError: (ErrorState) -> Unit,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        inScope: CoroutineScope = screenModelScope,
    ): Job {
        return runWithErrorCheck(onError, inScope,dispatcher) {
            val result = function()
            onSuccess(result)
        }
    }

    protected fun <T> tryToCollect(
        function: suspend () -> Flow<T?>,
        onNewValue: (T) -> Unit,
        onError: (ErrorState) -> Unit,
        dispatcher: CoroutineDispatcher,
        inScope: CoroutineScope = screenModelScope,
    ): Job {
        return runWithErrorCheck(onError, inScope, dispatcher) {
            function().distinctUntilChanged().collectLatest {
                it?.let { onNewValue(it) }
            }
        }
    }

    protected fun updateState(updater: S.() -> S) {
        _state.update(updater)
    }

    protected fun sendNewEffect(newEffect: E) {
        screenModelScope.launch(Dispatchers.IO) {
            _effect.emit(newEffect)
        }
    }

    private fun runWithErrorCheck(
        onError: (ErrorState) -> Unit,
        inScope: CoroutineScope = screenModelScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        function: suspend () -> Unit,
    ): Job {
        return inScope.launch(dispatcher) {
            try {
                function()
            } catch (exception: AuthenticationException) {
                exception.printStackTrace()
                handelAuthorizationException(exception, onError)
            } catch (exception: Exception) {
                exception.printStackTrace()
                onError(ErrorState.SomethingWentWrong(exception.message))
            }
        }
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