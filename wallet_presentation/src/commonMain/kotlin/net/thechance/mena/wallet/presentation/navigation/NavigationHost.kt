package net.thechance.mena.wallet.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import net.thechance.mena.wallet.domain.exceptions.UnknownNetworkException
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
    navigateBack: () -> Unit = {},
    updateBottomNavigationVisibility: (Boolean) -> Unit,
) {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()

    LaunchedEffect(currentRoute) {
        currentRoute?.destination?.route.let { route ->
            updateBottomNavigationVisibility(RoutesWithBottomNavigation.contains(route))
        }
    }

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
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
            composable<WalletMainScreenRoute> { WalletMainScreen(navigateBack = navigateBack) }
            composable<TransactionsHistoryScreenRoute> { TransactionHistoryScreen() }
            composable<TransactionDetailsScreenRoute> { TransactionDetailsScreen() }
            composable<ExportTransactionsScreenRoute> { ExportTransactionScreen() }
            composable<StatementsHistoryScreenRoute> { StatementHistoryScreen() }
            composable<ConfirmPaymentScreenRoute> {
                ConfirmPaymentScreen(navigateBack = navigateBack)
            }
            composable<PaymentResultScreenRoute> {
                PaymentResultScreen(navigateBack = navigateBack)
            }
            composable<StatementDetailsScreenRoute>(
                typeMap = mapOf(typeOf<StorageLocation>() to StorageLocationNavType)
            ) { backStackEntry ->
                StatementDetailsScreen(
                    statementLocation = backStackEntry.toRoute<StatementDetailsScreenRoute>().statementLocation
                )
            }
        }
    }
}

val LocalNavController = compositionLocalOf<NavController> {
    throw UnknownNetworkException("nav controller not provided")
}

private val RoutesWithBottomNavigation = listOf(
    WalletMainScreenRoute::class.qualifiedName,
    TransactionsHistoryScreenRoute::class.qualifiedName,
    StatementsHistoryScreenRoute::class.qualifiedName
)