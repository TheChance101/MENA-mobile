package net.thechance.mena.wallet.presentation.navigation

import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.domain.model.TransactionStatus
import net.thechance.mena.wallet.domain.model.TransactionType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
sealed class WalletRoute

@Serializable
data object WalletMainScreenRoute: WalletRoute()

@Serializable
data object TransactionsHistoryScreenRoute: WalletRoute()

@Serializable
data object StatementsHistoryScreenRoute : WalletRoute()

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class StatementDetailsScreenRoute(val id: String) : WalletRoute()

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class TransactionDetailsScreenRoute(
    val id: String
): WalletRoute() {
    init {
        Uuid.parse(id)
    }
}

@Serializable
data object ExportTransactionsScreenRoute: WalletRoute()

@Serializable
data class ViewTransactionsStatementScreenRoute(
    val types: List<TransactionType>? = null,
    val status: TransactionStatus? = null,
    val startDate: String? = null,
    val endDate: String? = null,
): WalletRoute()

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class ConfirmPaymentScreenRoute(
    val id: String,
    val amount: Double
): WalletRoute() {
    init {
        Uuid.parse(id)
    }
}