package net.thechance.mena.faith.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.error_coordinates
import mena.faith_presentation.generated.resources.error_latitude
import mena.faith_presentation.generated.resources.error_longitude
import mena.faith_presentation.generated.resources.error_network
import mena.faith_presentation.generated.resources.error_no_internet
import mena.faith_presentation.generated.resources.error_unauthorized
import mena.faith_presentation.generated.resources.error_unknown
import mena.faith_presentation.generated.resources.surah_download_failed
import net.thechance.mena.faith.domain.annotation.KoverIgnore
import net.thechance.mena.faith.domain.exception.FaithException
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoverIgnore
abstract class BaseViewModel<UI_STATE, UI_EFFECT>(
    initialState: UI_STATE,
) : ViewModel(), KoinComponent {

    private val snackbarHandler: SnackbarHandler by inject()
    private var snackbarJob: Job? = null
    private val debounceDelay = 200L

    val snackBarState = snackbarHandler.snackBarState

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

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

    protected fun <T> tryToExecute(
        execute: suspend () -> T,
        onSuccess: (suspend (T) -> Unit)? = null,
        onError: (ErrorState) -> Unit = {},
        onStart: suspend () -> Unit = {},
        onFinally: () -> Unit = {},
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        inScope: CoroutineScope = viewModelScope,
        delayMillis: Long = 0L
    ): Job {
        val handler = CoroutineExceptionHandler { _, throwable ->
            onError(mapExceptionToErrorState(throwable))
        }

        return inScope.launch(dispatcher + handler) {
            onStart()
            delay(delayMillis)
            runCatching { execute() }
                .onSuccess { result -> onSuccess?.invoke(result) }
                .onFailure { throwable -> onError(mapExceptionToErrorState(throwable)) }
            onFinally()
        }
    }

    protected fun <T> tryToCollect(
        onError: suspend (ErrorState) -> Unit = {},
        onEmitNewValue: (T) -> Unit = {},
        coroutineScope: CoroutineScope = viewModelScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend () -> Flow<T>
    ) {
        coroutineScope.launch(dispatcher) {
            try {
                block()
                    .catch {
                        onError(mapExceptionToErrorState(it))
                    }.collect {
                        onEmitNewValue(it)
                    }
            } catch (e: Throwable) {
                onError(mapExceptionToErrorState(e))
            }
        }
    }

    protected fun mapExceptionToErrorState(throwable: Throwable): ErrorState {
        val faithException = throwable as? FaithException ?: FaithException.UnknownException
        return ErrorState(
            message = mapExceptionToMessage(faithException),
            exception = faithException
        )
    }

    private fun mapExceptionToMessage(exception: FaithException) = when (exception) {
        FaithException.NetworkException -> Res.string.error_network
        FaithException.NoInternetException -> Res.string.error_no_internet
        FaithException.UnauthorizedException -> Res.string.error_unauthorized
        FaithException.UnknownException -> Res.string.error_unknown
        FaithException.InvalidLatitudeException -> Res.string.error_latitude
        FaithException.InvalidLongitudeException -> Res.string.error_longitude
        FaithException.FailedToDownloadSurahException -> Res.string.surah_download_failed
        FaithException.FileCreationException -> Res.string.surah_download_failed
        FaithException.UrlCreationException -> Res.string.surah_download_failed
        FaithException.InvalidCoordinates -> Res.string.error_coordinates
    }

    protected fun handleSuccessSnackBar(message: StringResource) {
        snackbarJob?.cancel()
        snackbarJob = viewModelScope.launch {
            delay(debounceDelay)
            snackbarHandler.showSnackBar(
                message = { getString(message) },
                status = SnackBarState.Status.Success,
                scope = viewModelScope
            )
        }
    }

    protected fun handleErrorSnackBar(error: ErrorState) {
        snackbarJob?.cancel()
        snackbarJob = viewModelScope.launch {
            delay(debounceDelay)
            snackbarHandler.showSnackBar(
                message = error.message,
                status = SnackBarState.Status.Error,
                scope = viewModelScope,
            )
        }
    }

}