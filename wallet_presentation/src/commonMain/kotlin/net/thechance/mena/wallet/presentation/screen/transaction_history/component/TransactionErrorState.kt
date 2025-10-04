package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.wallet.presentation.component.NoInternetScreen

@Composable
fun TransactionErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        NoInternetScreen(onRetry = onRetry)
    }
}