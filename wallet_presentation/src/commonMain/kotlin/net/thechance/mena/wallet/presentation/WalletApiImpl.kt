package net.thechance.mena.wallet.presentation

import androidx.compose.runtime.Composable
import net.thechance.mena.wallet.api.WalletApi
import net.thechance.mena.wallet.presentation.navigation.ConfirmPaymentScreenRoute
import net.thechance.mena.wallet.presentation.navigation.NavigationHost
import net.thechance.mena.wallet.presentation.navigation.WalletMainScreenRoute
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Single([WalletApi::class])
class WalletApiImpl : WalletApi {
    @Composable
    override fun WalletEntry(
        navigateBack: () -> Unit,
        updateBottomNavigationVisibility: (Boolean) -> Unit,
    ) {
        NavigationHost(
            startDestination = WalletMainScreenRoute,
            navigateBack = navigateBack,
            updateBottomNavigationVisibility = updateBottomNavigationVisibility
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    @Composable
    override fun ConfirmPaymentEntry(
        transactionId: Uuid,
        navigateBack: () -> Unit,
        updateBottomNavigationVisibility: (Boolean) -> Unit,
    ) {
        NavigationHost(
            startDestination = ConfirmPaymentScreenRoute(
                transactionId = transactionId.toString()
            ),
            navigateBack = navigateBack,
            updateBottomNavigationVisibility = updateBottomNavigationVisibility
        )
    }
}