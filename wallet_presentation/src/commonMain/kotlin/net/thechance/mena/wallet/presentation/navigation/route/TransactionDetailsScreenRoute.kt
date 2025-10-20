package net.thechance.mena.wallet.presentation.navigation.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreen
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class TransactionDetailsScreenRoute(
    val id: String
) : WalletRoute() {
    init {
        Uuid.parse(id)
    }
}

fun NavGraphBuilder.transactionDetailsScreenRoute(navController: NavController){
    composable<TransactionDetailsScreenRoute> { backStackEntry ->
        TransactionDetailsScreen(
            onNavigateBackClicked = { navController.popBackStack() },
            id = backStackEntry.toRoute<TransactionDetailsScreenRoute>().id
        )
    }
}