@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transaction_details

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import io.github.suwasto.capturablecompose.CompressionFormat
import io.github.suwasto.capturablecompose.toByteArray
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
import mena.wallet_presentation.generated.resources.purchase
import mena.wallet_presentation.generated.resources.receive
import mena.wallet_presentation.generated.resources.receive_button
import mena.wallet_presentation.generated.resources.send
import mena.wallet_presentation.generated.resources.send_button
import mena.wallet_presentation.generated.resources.success
import mena.wallet_presentation.generated.resources.success_icon
import mena.wallet_presentation.generated.resources.to
import mena.wallet_presentation.generated.resources.transfer
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.presentation.utils.formateTransactionDate
import kotlin.uuid.ExperimentalUuidApi

fun Transaction.toUi() = TransactionDetailsScreenState.TransactionDetailsUiState(
    id = id.toString(),
    amount = amount.toString(),
    date = formateTransactionDate(createdAt),
    userName = when (type) {
        Transaction.Type.SENT -> senderName
        Transaction.Type.RECEIVED -> receiverName
        Transaction.Type.ONLINE_PURCHASE -> senderName
    },
    otherParty = when (type) {
        Transaction.Type.SENT -> receiverName
        Transaction.Type.RECEIVED -> senderName
        Transaction.Type.ONLINE_PURCHASE -> receiverName
    },
    transactionType = type,
    transactionStatus = status
)

fun imageBitmapToByteArray(imageBitmap: ImageBitmap) =
    imageBitmap.toByteArray(CompressionFormat.PNG, 100)

fun getTransactionTypeText(type : Transaction.Type) = when (type) {
    Transaction.Type.ONLINE_PURCHASE -> Res.string.pay
    Transaction.Type.SENT -> Res.string.send
    Transaction.Type.RECEIVED -> Res.string.receive
}

fun getTransactionTypeIcon(type : Transaction.Type) = when (type) {
    Transaction.Type.ONLINE_PURCHASE -> Res.drawable.ic_pay
    Transaction.Type.SENT -> Res.drawable.ic_send
    Transaction.Type.RECEIVED -> Res.drawable.ic_receive
}

fun getTransactionTypeIconDescription(type : Transaction.Type) = when (type) {
    Transaction.Type.ONLINE_PURCHASE -> Res.string.pay_button
    Transaction.Type.SENT -> Res.string.send_button
    Transaction.Type.RECEIVED -> Res.string.receive_button
}

fun getUserInfo(type : Transaction.Type) = when (type) {
    Transaction.Type.SENT, Transaction.Type.ONLINE_PURCHASE -> Res.string.from
    Transaction.Type.RECEIVED -> Res.string.to
}

fun getStatusContent(status : Transaction.Status) = when (status) {
    Transaction.Status.FAIL -> Res.string.failed
    Transaction.Status.SUCCESS -> Res.string.success
}

fun getStatusIcon(status : Transaction.Status) = when (status) {
    Transaction.Status.FAIL -> Res.drawable.ic_failed
    Transaction.Status.SUCCESS -> Res.drawable.ic_success
}

fun getStatusIconDescription(status : Transaction.Status) = when (status) {
    Transaction.Status.FAIL -> Res.string.failed_icon
    Transaction.Status.SUCCESS -> Res.string.success_icon
}

@Composable
fun getIconTint(status : Transaction.Status) = when (status) {
    Transaction.Status.FAIL -> Theme.colorScheme.error
    Transaction.Status.SUCCESS -> Theme.colorScheme.success
}

fun getTypeContent(type : Transaction.Type) = when (type) {
    Transaction.Type.SENT, Transaction.Type.RECEIVED -> Res.string.transfer
    Transaction.Type.ONLINE_PURCHASE -> Res.string.purchase
}

fun getOtherPartyTitle(type : Transaction.Type) = when (type) {
    Transaction.Type.SENT, Transaction.Type.ONLINE_PURCHASE -> Res.string.to
    Transaction.Type.RECEIVED -> Res.string.from
}