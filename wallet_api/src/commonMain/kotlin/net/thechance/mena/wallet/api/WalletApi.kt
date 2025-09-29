package net.thechance.mena.wallet.api

import androidx.compose.runtime.Composable

interface WalletApi {
    @Composable
    fun WalletEntry(navigateBack: () -> Unit = {})
}