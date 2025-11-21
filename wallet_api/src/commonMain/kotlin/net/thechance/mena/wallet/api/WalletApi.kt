package net.thechance.mena.wallet.api

import androidx.compose.runtime.Composable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface WalletApi {
    @Composable
    fun WalletEntry(
        navigateBack: () -> Unit,
        updateBottomNavigationVisibility: (Boolean) -> Unit,
    )

    @OptIn(ExperimentalUuidApi::class)
    @Composable
    fun ConfirmPaymentEntry(
        transactionId: Uuid,
        navigateBack: () -> Unit,
        updateBottomNavigationVisibility: (Boolean) -> Unit,
    )
}