package net.thechance.mena.wallet.presentation.screen.wallet.mappar

import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.exceptions.UnknownException
import net.thechance.mena.wallet.presentation.base.ErrorType
import net.thechance.mena.wallet.presentation.base.UiState

fun Throwable.toUiError(): UiState.Error {
    return when (this) {
        is NoInternetException -> UiState.Error(
            throwable = this,
            errorType = ErrorType.NO_INTERNET,
            message = this.message
        )
        is UnknownException -> UiState.Error(
            throwable = this,
            errorType = ErrorType.UNKNOWN,
            message = this.message
        )
        else -> UiState.Error(
            throwable = this,
            errorType = ErrorType.UNKNOWN,
            message = this.message ?: UNKNOWN_ERROR
        )
    }
}
const val UNKNOWN_ERROR = "Unknown Error"