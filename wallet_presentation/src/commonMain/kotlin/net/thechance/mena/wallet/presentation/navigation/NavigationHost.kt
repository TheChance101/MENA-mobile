package net.thechance.mena.wallet.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.wallet.presentation.navigation.navType.StorageLocationNavType
import net.thechance.mena.wallet.presentation.navigation.route.WalletMainScreenRoute
import net.thechance.mena.wallet.presentation.navigation.route.WalletRoute
import net.thechance.mena.wallet.presentation.navigation.route.confirmPaymentScreenRoute
import net.thechance.mena.wallet.presentation.navigation.route.exportTransactionsScreenRoute
import net.thechance.mena.wallet.presentation.navigation.route.paymentResultScreenRoute
import net.thechance.mena.wallet.presentation.navigation.route.statementDetailsScreenRoute
import net.thechance.mena.wallet.presentation.navigation.route.statementsHistoryScreenRoute
import net.thechance.mena.wallet.presentation.navigation.route.transactionDetailsScreenRoute
import net.thechance.mena.wallet.presentation.navigation.route.transactionsHistoryScreenRoute
import net.thechance.mena.wallet.presentation.navigation.route.walletMainScreenRoute
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
        walletMainScreenRoute(navController, navigateBack)
        transactionsHistoryScreenRoute(navController)
        transactionDetailsScreenRoute(navController)
        exportTransactionsScreenRoute(navController)
        statementDetailsScreenRoute(navController)
        statementsHistoryScreenRoute(navController)
        confirmPaymentScreenRoute(navController)
        paymentResultScreenRoute(navController)
    }
}