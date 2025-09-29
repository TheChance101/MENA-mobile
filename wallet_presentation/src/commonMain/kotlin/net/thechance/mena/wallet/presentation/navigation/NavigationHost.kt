package net.thechance.mena.wallet.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import net.thechance.mena.wallet.presentation.screen.export_transactions.ExportTransactionsScreen
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreen
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreen
import net.thechance.mena.wallet.presentation.screen.view_transactions_statement.ViewTransactionStatementScreen
import net.thechance.mena.wallet.presentation.screen.wallet.WalletMainScreen
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

const val TransitionDuration = 300

@OptIn(ExperimentalUuidApi::class)
@Composable
fun NavigationHost(
    startDestination: WalletRoute = WalletMainScreenRoute,
    navigateBack: () -> Unit = {}
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(durationMillis = TransitionDuration)) },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = TransitionDuration,
                    delayMillis = TransitionDuration
                )
            )
        },
        popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = TransitionDuration)) },
        popExitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = TransitionDuration,
                    delayMillis = TransitionDuration
                )
            )
        },
    ) {
        composable<WalletMainScreenRoute> {
            WalletMainScreen(
                onNavigateBackClicked = navigateBack,
                navigateToTransactionHistory = {
                    navController.navigate(TransactionsHistoryScreenRoute)
                }
            )
        }
        composable<TransactionsHistoryScreenRoute> {
            TransactionHistoryScreen(
                onNavigateBackClicked = { navController.popBackStack() },
                navigateToTransactionDetails = {
                    navController.navigate(TransactionDetailsScreenRoute(it.toString()))
                },
                navigateToExportTransaction = {
                    navController.navigate(ExportTransactionsScreenRoute)
                }
            )
        }
        composable<TransactionDetailsScreenRoute> { backStackEntry ->
            TransactionDetailsScreen(
                onNavigateBackClicked = { navController.popBackStack() },
                id = backStackEntry.toRoute<TransactionDetailsScreenRoute>().id.let(Uuid::parse)
            )
        }
        composable<ExportTransactionsScreenRoute> {
            ExportTransactionsScreen(
                onNavigateBackClicked = { navController.popBackStack() },
                navigateToVewTransactionStatement = {
                    navController.navigate(ViewTransactionsStatementScreenRoute)
                }
            )
        }
        composable<ViewTransactionsStatementScreenRoute> {
            ViewTransactionStatementScreen(
                onNavigateBackClicked = { navController.popBackStack() }
            )
        }
    }
}