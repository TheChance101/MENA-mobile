package net.thechance.mena.wallet.presentation.base

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val throwable: Throwable? = null) : UiState<Nothing>()

    val UiState<*>.isLoading get() = this is UiState.Loading
    val UiState<*>.isSuccess get() = this is UiState.Success<*>
    val UiState<*>.isError get() = this is UiState.Error
}