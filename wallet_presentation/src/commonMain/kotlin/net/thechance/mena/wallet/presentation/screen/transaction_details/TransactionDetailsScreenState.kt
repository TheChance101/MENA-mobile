package net.thechance.mena.wallet.presentation.screen.transaction_details

import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.failed
import mena.wallet_presentation.generated.resources.failed_icon
import mena.wallet_presentation.generated.resources.from
import mena.wallet_presentation.generated.resources.ic_failed
import mena.wallet_presentation.generated.resources.ic_pay
import mena.wallet_presentation.generated.resources.ic_receive
import mena.wallet_presentation.generated.resources.ic_send
import mena.wallet_presentation.generated.resources.ic_success
import mena.wallet_presentation.generated.resources.pay
import mena.wallet_presentation.generated.resources.pay_button
import mena.wallet_presentation.generated.resources.receive
import mena.wallet_presentation.generated.resources.receive_button
import mena.wallet_presentation.generated.resources.send
import mena.wallet_presentation.generated.resources.send_button
import mena.wallet_presentation.generated.resources.success
import mena.wallet_presentation.generated.resources.success_icon
import mena.wallet_presentation.generated.resources.to
import mena.wallet_presentation.generated.resources.transfer
import net.thechance.mena.wallet.presentation.base.SnackBarState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class TransactionDetailsScreenState(
    val transactionDetailsUiState: TransactionDetailsUiState = TransactionDetailsUiState(),
    val isLoading: Boolean = false,
    val isError: Throwable? = null,
    val isShareReceiptBtnLoading: Boolean = false,
    val snackBar: SnackBarState = SnackBarState(),
){
    data class TransactionDetailsUiState(
        val id: String = "",
        val amount: String = "",
        val date: String = "",
        val userName: String = "",
        val otherParty: String = "",
        val transactionType: TransactionTypeUiState = TransactionTypeUiState.ONLINE_PURCHASE,
        val transactionStatus: TransactionStatusUiState = TransactionStatusUiState.FAILED,
        val userInfo: StringResource = Res.string.from,
        val typeContent: StringResource = Res.string.transfer,
        val otherPartyTitle: StringResource = Res.string.to
    )
    enum class TransactionTypeUiState(
        val titleRes: StringResource,
        val iconRes: DrawableResource,
        val iconContentDescriptionRes: StringResource
    ) {
        ONLINE_PURCHASE(
            titleRes = Res.string.pay,
            iconRes = Res.drawable.ic_pay,
            iconContentDescriptionRes = Res.string.pay_button
        ),
        SENT(
            titleRes = Res.string.send,
            iconRes = Res.drawable.ic_send,
            iconContentDescriptionRes = Res.string.send_button
        ),
        RECEIVED(
            titleRes = Res.string.receive,
            iconRes = Res.drawable.ic_receive,
            iconContentDescriptionRes = Res.string.receive_button
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
            iconContentDescriptionRes = Res.string.failed_icon
        ),
        SUCCESS(
            contentRes = Res.string.success,
            iconRes = Res.drawable.ic_success,
            iconContentDescriptionRes = Res.string.success_icon
        )
    }
}