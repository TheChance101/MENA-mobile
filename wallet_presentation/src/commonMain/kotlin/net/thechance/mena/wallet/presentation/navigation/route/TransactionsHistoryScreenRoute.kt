package net.thechance.mena.wallet.presentation.navigation.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreen
import kotlin.uuid.ExperimentalUuidApi

@Serializable
data object TransactionsHistoryScreenRoute : WalletRoute()

@OptIn(ExperimentalUuidApi::class)
fun NavGraphBuilder.transactionsHistoryScreenRoute(navController: NavController){
    composable<TransactionsHistoryScreenRoute> {
        TransactionHistoryScreen()
    }
}