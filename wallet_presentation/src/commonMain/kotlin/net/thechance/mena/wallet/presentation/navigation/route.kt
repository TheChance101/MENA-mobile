package net.thechance.mena.wallet.presentation.navigation

import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.utils.StorageLocation

@Serializable
sealed class WalletRoute

@Serializable
data object WalletMainScreenRoute : WalletRoute()

@Serializable
data object TransactionsHistoryScreenRoute : WalletRoute()

@Serializable
data class TransactionDetailsScreenRoute(
    val id: String
) : WalletRoute()

@Serializable
data class ConfirmPaymentScreenRoute(
    val transactionId: String
) : WalletRoute()

@Serializable
data object ExportTransactionsScreenRoute : WalletRoute()

@Serializable
data class PaymentResultScreenRoute(
    val transactionId: String,
    val submitTransactionResultStatus: String,
    val receiverName: String,
    val amount: Double
) : WalletRoute()

@Serializable
data class StatementDetailsScreenRoute(val statementLocation: StorageLocation) : WalletRoute()

@Serializable
data object StatementsHistoryScreenRoute : WalletRoute()