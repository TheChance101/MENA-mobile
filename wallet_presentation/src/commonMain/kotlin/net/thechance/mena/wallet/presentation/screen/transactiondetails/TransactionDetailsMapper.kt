@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transactiondetails

import androidx.compose.ui.graphics.ImageBitmap
import io.github.suwasto.capturablecompose.CompressionFormat
import io.github.suwasto.capturablecompose.toByteArray
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