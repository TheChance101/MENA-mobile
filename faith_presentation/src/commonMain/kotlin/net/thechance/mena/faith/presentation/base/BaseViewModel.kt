package net.thechance.mena.faith.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<UI_STATE, UI_EFFECT>(
    initialState: UI_STATE
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<UI_EFFECT>()
    val uiEffect = _uiEffect.asSharedFlow()

    protected val currentState: UI_STATE
        get() = _uiState.value

    protected fun updateState(updater: (UI_STATE) -> UI_STATE) {
        _uiState.update(updater)
    }

    protected fun sendEffect(effect: UI_EFFECT) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }

    protected fun <T> tryToExecute(
        callee: suspend () -> T,
        onSuccess: ((T) -> Unit)? = null,
        onError: (Throwable) -> Unit = {},
        onStart: suspend () -> Unit = {},
        onFinally: () -> Unit = {},
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): Job {
        val handler = CoroutineExceptionHandler { _, throwable ->
            onError(throwable)
        }

        return viewModelScope.launch(dispatcher + handler) {
            onStart()
            runCatching { callee() }
                .onSuccess { result -> onSuccess?.invoke(result) }
                .onFailure { throwable -> onError(throwable) }
            onFinally()
        }
    }
}
