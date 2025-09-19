package net.thechance.mena.wallet.presentation.screen.wallet.mappar

import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.exceptions.UnknownException
import net.thechance.mena.wallet.presentation.base.ErrorType
import net.thechance.mena.wallet.presentation.base.UiState

fun Throwable.toUiError(): UiState.Error {
    return when (this) {
        is NoInternetException -> UiState.Error(
            errorType = ErrorType.NO_INTERNET
        )

        is UnknownException -> UiState.Error(
            errorType = ErrorType.UNKNOWN
        )
        else -> UiState.Error(
            errorType = ErrorType.UNKNOWN,
        )
    }
}