package net.thechance.mena.wallet.presentation.navigation

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
open class WalletRoute

@Serializable
data object WalletMainScreenRoute: WalletRoute()

@Serializable
data object TransactionsHistoryScreenRoute: WalletRoute()

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
data object ViewTransactionsStatementScreenRoute: WalletRoute()