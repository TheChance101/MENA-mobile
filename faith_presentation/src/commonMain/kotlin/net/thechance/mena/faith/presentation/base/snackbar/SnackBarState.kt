package net.thechance.mena.faith.presentation.base.snackbar


data class SnackBarState(
    val message: String = "",
    val status: Status = Status.Success,
    val isVisible: Boolean = false
) {
    enum class Status {
        Success, Error
    }
}
