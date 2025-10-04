package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun TransactionHistoryDivider() {
    Box(
        modifier = Modifier
            .padding(top = 4.dp)
            .fillMaxWidth(1f)
            .height(1.dp)
            .background(Theme.colorScheme.stroke)
    )
}