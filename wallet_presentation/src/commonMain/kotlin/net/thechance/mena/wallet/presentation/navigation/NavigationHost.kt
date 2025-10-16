package net.thechance.mena.wallet.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import net.thechance.mena.wallet.presentation.navigation.navType.StorageLocationNavType
import net.thechance.mena.wallet.presentation.screen.confirm_payment.ConfirmPaymentScreen
import net.thechance.mena.wallet.presentation.screen.export.ExportTransactionScreen
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultScreen
import net.thechance.mena.wallet.presentation.screen.statement_details.StatementDetailsScreen
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementHistoryScreen
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreen
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreen
import net.thechance.mena.wallet.presentation.screen.wallet.WalletMainScreen
import net.thechance.mena.wallet.presentation.utils.StorageLocation
import kotlin.reflect.typeOf
import kotlin.uuid.ExperimentalUuidApi

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
        typeMap = mapOf(typeOf<StorageLocation>() to StorageLocationNavType)
    ) {
        composable<WalletMainScreenRoute> {
            WalletMainScreen(
                onNavigateBackClicked = navigateBack,
                navigateToTransactionHistory = {
                    navController.navigate(TransactionsHistoryScreenRoute)
                },
                navigateToStatementsHistory = {
                    navController.navigate(StatementsHistoryScreenRoute)
                },
                navigateToPaymentScreen = { amount, transactionId ->
                    navController.navigate(
                        ConfirmPaymentScreenRoute(
                            amount = amount,
                            transactionId = transactionId.toString()
                        )
                    )
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
                id = backStackEntry.toRoute<TransactionDetailsScreenRoute>().id
            )
        }
        composable<ExportTransactionsScreenRoute> {
            ExportTransactionScreen(
                onNavigateBackClicked = { navController.popBackStack() },
                navigateToStatementDetails = { statementLocation ->
                    navController.navigate(StatementDetailsScreenRoute(statementLocation))
                }
            )
        }
        composable<StatementDetailsScreenRoute>(
            typeMap = mapOf(typeOf<StorageLocation>() to StorageLocationNavType)
        ) { backStackEntry ->
            val statementLocation = backStackEntry.toRoute<StatementDetailsScreenRoute>().statementLocation

            StatementDetailsScreen(
                onNavigateBackClicked = { navController.popBackStack() },
                statementLocation = statementLocation
            )
        }

        composable<StatementsHistoryScreenRoute> {
            StatementHistoryScreen(
                onNavigateBackClicked = { navController.popBackStack() },
                navigateToStatementDetails = { navController.navigate(StatementDetailsScreenRoute(statementLocation = it)) },
            )
        }

        composable<ConfirmPaymentScreenRoute> { backStackEntry ->
            ConfirmPaymentScreen(
                onNavigateBackClicked = navController::popBackStack,
                transactionId = backStackEntry.toRoute<ConfirmPaymentScreenRoute>().transactionId,
                amount = backStackEntry.toRoute<ConfirmPaymentScreenRoute>().amount,
                navigateToPaymentResultScreen = { receiverName, amount, transactionId, submitTransactionResultStatus ->
                    navController.navigate(
                        PaymentResultScreenRoute(
                            transactionId = transactionId.toString(),
                            submitTransactionResultStatus = submitTransactionResultStatus.name,
                            amount = amount,
                            receiverName = receiverName
                        )
                    )
                }
            )
        }
        composable<PaymentResultScreenRoute> { backStackEntry ->
            PaymentResultScreen(
                transactionId = backStackEntry.toRoute<PaymentResultScreenRoute>().transactionId,
                submitTransactionResultStatus = backStackEntry.toRoute<PaymentResultScreenRoute>().submitTransactionResultStatus,
                receiverName = backStackEntry.toRoute<PaymentResultScreenRoute>().receiverName,
                amount = backStackEntry.toRoute<PaymentResultScreenRoute>().amount,
                onNavigateBackClicked = { navController.popBackStack() },
                onNavigateToTransactionDetailsClicked = { receiverId ->
                    navController.navigate(TransactionDetailsScreenRoute(receiverId))
                },
                onCancelClicked = {
                    navController.navigate(WalletMainScreenRoute) {
                        popUpTo(WalletMainScreenRoute) { inclusive = true }
                    }
                }
            )
        }
    }
}