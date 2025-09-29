package net.thechance.mena.wallet.presentation.screen.view_transactions

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.wallet.presentation.base.SnackBarState
data class ViewTransactionsScreenState(
    val isLoading: Boolean = false,
    val bitmap: ImageBitmap? = null,
    val pdfBytes: ByteArray? = null,
    val fileName: String = "transaction_statement",
    val isShareLoading: Boolean = false,
    val snackBar: SnackBarState = SnackBarState()
)