package net.thechance.mena.wallet.presentation.base

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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.wallet.domain.exceptions.BlockedReceiverException
import net.thechance.mena.wallet.domain.exceptions.NoDataFoundException
import net.thechance.mena.wallet.domain.exceptions.NoInternetException

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
        inScope: CoroutineScope = viewModelScope
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            inScope.launch {
                onError(mapError(throwable))
                onFinish?.invoke()
            }
        }

        return inScope.launch(dispatcher + exceptionHandler) {
            onStart?.invoke()
            onSuccess(callee())
            onFinish?.invoke()
        }
    }

    private fun mapError(throwable: Throwable): ErrorState {
        return when (throwable) {
            is NoInternetException -> ErrorState.NoInternet
            is NoDataFoundException -> ErrorState.NoDataFound
            is BlockedReceiverException -> ErrorState.BlockedReceiver
            else -> ErrorState.UnknownError
        }
    }
}