package net.thechance.mena.wallet.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.wallet.domain.exceptions.NoDataFoundException
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import kotlin.coroutines.cancellation.CancellationException

abstract class BaseViewModel<STATE, EFFECT>(initialState: STATE) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _uiEffect = MutableSharedFlow<EFFECT>()
    val uiEffect = _uiEffect.asSharedFlow()

    protected val currentState: STATE
        get() = _state.value

    protected fun updateState(updater: (STATE) -> STATE) {
        _state.update(updater)
    }

    protected fun sendEffect(effect: EFFECT) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }

    protected fun <T> tryToExecute(
        callee: suspend () -> T,
        onSuccess: (suspend (T) -> Unit),
        onError: (suspend (ErrorState) -> Unit),
        onStart: (suspend () -> Unit)? = null,
        onFinish: (suspend () -> Unit)? = null,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): Job {
        return viewModelScope.launch(dispatcher) {
            try {
                safeInvoke(onStart)
                val result = callee()
                safeInvoke { onSuccess(result) }
            } catch (e: CancellationException) {
                throw e
            } catch (t: Throwable) {
                safeInvoke { onError(mapError(t)) }
            } finally {
                safeInvoke(onFinish)
            }
        }
    }

    private suspend fun safeInvoke(block: (suspend () -> Unit)?) {
        try {
            block?.invoke()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) { }
    }

    private fun mapError(throwable: Throwable): ErrorState {
        return when (throwable) {
            is NoInternetException -> ErrorState.NoInternet
            is NoDataFoundException -> ErrorState.NoDataFound
            else -> ErrorState.UnknownError
        }
    }
}