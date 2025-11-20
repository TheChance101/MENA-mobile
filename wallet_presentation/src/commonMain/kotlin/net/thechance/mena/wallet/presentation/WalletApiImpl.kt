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
        currentBottomNavigationVisibility: Boolean
    ) {
        updateBottomNavigationVisibility(HIDE_BOTTOM_NAVIGATION)
        NavigationHost(
            startDestination = WalletMainScreenRoute,
            navigateBack = {
                updateBottomNavigationVisibility(currentBottomNavigationVisibility)
                navigateBack()
            }
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    @Composable
    override fun ConfirmPaymentEntry(
        transactionId: Uuid,
        navigateBack: () -> Unit,
        updateBottomNavigationVisibility: (Boolean) -> Unit,
        currentBottomNavigationVisibility : Boolean
    ) {
        updateBottomNavigationVisibility(HIDE_BOTTOM_NAVIGATION)
        NavigationHost(
            startDestination = ConfirmPaymentScreenRoute(
                transactionId = transactionId.toString()
            ),
            navigateBack = {
                updateBottomNavigationVisibility(currentBottomNavigationVisibility)
                navigateBack()
            }
        )
    }

    companion object {
        const val HIDE_BOTTOM_NAVIGATION = false
    }
}