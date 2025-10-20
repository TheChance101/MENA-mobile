package net.thechance.mena.wallet.presentation.navigation.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.screen.export.ExportTransactionScreen

@Serializable
data object ExportTransactionsScreenRoute : WalletRoute()

fun NavGraphBuilder.exportTransactionsScreenRoute(navController: NavController){
    composable<ExportTransactionsScreenRoute> {
        ExportTransactionScreen(
            onNavigateBackClicked = { navController.popBackStack() },
            navigateToStatementDetails = { statementLocation ->
                navController.navigate(StatementDetailsScreenRoute(statementLocation))
            }
        )
    }
}