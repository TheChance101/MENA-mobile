package net.thechance.mena.wallet.presentation.screen.transaction_history

import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.failed
import mena.wallet_presentation.generated.resources.ic_failed
import mena.wallet_presentation.generated.resources.ic_receive
import mena.wallet_presentation.generated.resources.ic_reload
import mena.wallet_presentation.generated.resources.ic_send
import mena.wallet_presentation.generated.resources.ic_shopping_bag
import mena.wallet_presentation.generated.resources.online_shopping
import mena.wallet_presentation.generated.resources.received
import mena.wallet_presentation.generated.resources.sent
import mena.wallet_presentation.generated.resources.success
import mena.wallet_presentation.generated.resources.transaction_receive
import mena.wallet_presentation.generated.resources.transaction_send
import net.thechance.mena.wallet.presentation.base.SnackBarState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class TransactionHistoryScreenState(
    val history: List<TransactionHistoryUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Throwable? = null,
    val snackBar: SnackBarState = SnackBarState(),
    val filterState: TransactionFilterState = TransactionFilterState(),
    val isFilterVisible: Boolean = false
) {
    data class TransactionHistoryUiState @OptIn(ExperimentalUuidApi::class) constructor(
        val id: Uuid,
        val timeAndDate: String = "",
        val amount: String = "",
        val type: TransactionTypeUiState = TransactionTypeUiState.ONLINE_SHOPPING,
        val status: TransactionStatusUiState = TransactionStatusUiState.FAILED,
        val contactName: String? = null
    )

    enum class TransactionTypeUiState(
        val titleRes: StringResource,
        val iconRes: DrawableResource,
        val iconContentDescriptionRes: StringResource
    ) {
        ONLINE_SHOPPING(
            titleRes = Res.string.online_shopping,
            iconRes = Res.drawable.ic_shopping_bag,
            iconContentDescriptionRes = Res.string.online_shopping
        ),
        SENT(
            titleRes = Res.string.transaction_send,
            iconRes = Res.drawable.ic_send,
            iconContentDescriptionRes = Res.string.sent
        ),
        RECEIVED(
            titleRes = Res.string.transaction_receive,
            iconRes = Res.drawable.ic_receive,
            iconContentDescriptionRes = Res.string.received
        )
    }

    enum class TransactionStatusUiState(
        val contentRes: StringResource,
        val iconRes: DrawableResource,
        val iconContentDescriptionRes: StringResource
    ) {
        FAILED(
            contentRes = Res.string.failed,
            iconRes = Res.drawable.ic_failed,
            iconContentDescriptionRes = Res.string.failed
        ),
        SUCCESS(
            contentRes = Res.string.success,
            iconRes = Res.drawable.ic_reload,
            iconContentDescriptionRes = Res.string.success
        )
    }
}