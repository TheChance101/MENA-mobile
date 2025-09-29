package net.thechance.mena.wallet.presentation

import androidx.compose.runtime.Composable
import net.thechance.mena.wallet.api.WalletApi
import net.thechance.mena.wallet.presentation.navigation.NavigationHost
import net.thechance.mena.wallet.presentation.navigation.WalletMainScreenRoute
import org.koin.core.annotation.Single

@Single([WalletApi::class])
class WalletApiImpl: WalletApi {
    @Composable
    override fun WalletEntry() {
        NavigationHost(startDestination = WalletMainScreenRoute)
    }
}