package net.thechance.mena.faith.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

@OptIn(FlowPreview::class)
abstract class BaseViewModel<UI_STATE, UI_EFFECT>(
    initialState: UI_STATE
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _snackBarState = MutableStateFlow(SnackBarState())
    val snackBarState = _snackBarState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<UI_EFFECT>()
    val uiEffect = _uiEffect.asSharedFlow()

    protected fun updateState(updater: (UI_STATE) -> UI_STATE) {
        _uiState.update(updater)
    }

    protected fun sendEffect(effect: UI_EFFECT) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }

    fun showSnackBar(
        message: StringResource,
        status: SnackBarState.Status,
        durationMillis: Long = 3000L,
    ) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            if (snackBarState.value.isVisible) {
                hideSnackBar()
                delay(1000L)
            }
            _snackBarState.update {
                SnackBarState(
                    message = message,
                    status = status,
                    isVisible = true
                )
            }
            delay(durationMillis)
            _snackBarState.update {
                it.copy(
                    isVisible = false
                )
            }
        }
    }

    private fun hideSnackBar() {
        viewModelScope.launch(Dispatchers.Main) {
            _snackBarState.update {
                it.copy(
                    isVisible = false,
                )
            }
        }
    }

    protected fun <T> tryToExecute(
        execute: suspend () -> T,
        onSuccess: ((T) -> Unit)? = null,
        onError: (Throwable) -> Unit = {},
        onStart: suspend () -> Unit = {},
        onFinally: () -> Unit = {},
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        inScope: CoroutineScope = viewModelScope
    ): Job {
        val handler = CoroutineExceptionHandler { _, throwable ->
            onError(throwable)
        }

        return inScope.launch(dispatcher + handler) {
            onStart()
            runCatching { execute() }
                .onSuccess { result -> onSuccess?.invoke(result) }
                .onFailure { throwable -> onError(throwable) }
            onFinally()
        }
    }
}
