package net.thechance.mena.wallet.presentation.screen.export

import net.thechance.mena.wallet.presentation.model.CustomToastState
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.model.FilterType

data class ExportTransactionsState(
    val snackBar: SnackBarState = SnackBarState(),
    val toast: CustomToastState = CustomToastState(),
    val isCustomFilterCardSelected: Boolean = false,
    val isDownloadLoading: Boolean = false,
    val isViewAndShareLoading: Boolean = false,
    val isViewAndShareButtonEnabled: Boolean = true,
    val isDownloadButtonEnabled: Boolean = true,
    val hasShownEmptyFileToast: Boolean = false,
    val noInternetConnection: Boolean = false,
    val selectedTransactionsTypes: Set<FilterType> = emptySet(),
    val startDate: String = "",
    val endDate: String = "",
    val hasNoTransactionsError: Boolean = false
) {
    val hasActiveFilters: Boolean
        get() = selectedTransactionsTypes.isNotEmpty()
}

