package net.thechance.mena.wallet.presentation.screen.export

import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.base.CustomToastState

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
    val selectedTransactionsTypes: Set<FilterType>? = null,
    val selectedTransactionsStatus: FilterStatus = FilterStatus.ALL,
    val startDate: String = "",
    val endDate: String = "",
)

