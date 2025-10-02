package net.thechance.mena.wallet.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun WalletScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit) ? = null,
    snackBar: (@Composable () -> Unit)? = null,
    overlays: (ScaffoldScope.() -> Unit)? = null,
    toast: (@Composable () -> Unit)? = null,
    backgroundColor: Color = Theme.colorScheme.background.surface,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .then(modifier)
    ) {
        Scaffold(
            topBar = { topBar?.invoke() },
            overlays = overlays ?: {},
            content = content
        )

        snackBar?.let { snackBarContent ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(start = 16.dp, end = 16.dp, top = 68.dp)
            ) {
                snackBarContent()
            }
        }
        toast?.let { toast ->
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                toast()
            }
        }
    }
}