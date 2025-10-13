package net.thechance.mena.wallet.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import net.thechance.mena.wallet.presentation.model.SubmitTransactionResultStatus
import net.thechance.mena.wallet.presentation.screen.confirm_payment.ConfirmPaymentScreen
import net.thechance.mena.wallet.presentation.screen.export.ExportTransactionScreen
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultScreen
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementHistoryScreen
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreen
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreen
import net.thechance.mena.wallet.presentation.screen.view_transactions_statement.ViewTransactionStatementScreen
import net.thechance.mena.wallet.presentation.screen.wallet.WalletMainScreen
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
                navigateToPaymentScreen = { amount, receiverId ->
                    navController.navigate(
                        ConfirmPaymentScreenRoute(
                            amount = amount,
                            id = receiverId.toString()
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
                navigateToVewTransactionStatement = { filterParams ->
                    navController.navigate(filterParams.toRoute())
                }
            )
        }
        composable<ViewTransactionsStatementScreenRoute> { backStackEntry ->
            val filterParams =
                backStackEntry.toRoute<ViewTransactionsStatementScreenRoute>().toFilterParams()
            ViewTransactionStatementScreen(
                onNavigateBackClicked = { navController.popBackStack() },
                filterParams = filterParams
            )
        }

        composable<StatementsHistoryScreenRoute> {
            StatementHistoryScreen(
                onNavigateBackClicked = { navController.popBackStack() },
                navigateToStatementDetails = { navController.navigate(StatementDetailsScreenRoute(id = it.toString())) },
            )
        }

        composable<StatementDetailsScreenRoute> { backStackEntry ->
            DummyScreen(title = "Statement Details")
        }
        composable<ConfirmPaymentScreenRoute> { backStackEntry ->
            ConfirmPaymentScreen(
                onNavigateBackClicked = navController::popBackStack,
                receiverId = backStackEntry.toRoute<ConfirmPaymentScreenRoute>().id,
                amount = backStackEntry.toRoute<ConfirmPaymentScreenRoute>().amount,
                navigateToPaymentResultScreen = { receiverName, amount ->
                    PaymentResultScreenRoute(
                        transactionId = "",
                        submitTransactionResultStatus = SubmitTransactionResultStatus.SUCCESS.name,
                        amount = amount,
                        receiverName = receiverName
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
                    TransactionDetailsScreenRoute(receiverId)
                },
                onCancelClicked = {
                    navController.popBackStack(
                        ConfirmPaymentScreenRoute,
                        inclusive = true
                    )
                }
            )
        }
    }
}