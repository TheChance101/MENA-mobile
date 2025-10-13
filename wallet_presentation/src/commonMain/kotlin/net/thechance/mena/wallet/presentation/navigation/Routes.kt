package net.thechance.mena.wallet.presentation.navigation

import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.utils.StorageLocation
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
data class StatementDetailsScreenRoute(val statementLocation: StorageLocation) : WalletRoute()

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