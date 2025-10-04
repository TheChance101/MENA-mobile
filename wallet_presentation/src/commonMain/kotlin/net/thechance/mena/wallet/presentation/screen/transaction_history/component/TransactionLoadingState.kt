package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun TransactionLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DotsProgressIndicator(
            modifier = Modifier.padding(10.dp),
            dotSize = 8.dp,
            colors = listOf(
                Theme.colorScheme.stroke,
                Theme.colorScheme.shadeTertiary,
                Theme.colorScheme.primary.primary
            )
        )
    }
}
