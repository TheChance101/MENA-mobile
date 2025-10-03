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
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.img_no_internet
import mena.wallet_presentation.generated.resources.no_internet_content
import mena.wallet_presentation.generated.resources.no_internet_title
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionHistoryEmpty
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WalletScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    snackBar: (@Composable () -> Unit)? = null,
    overlays: (ScaffoldScope.() -> Unit)? = null,
    toast: (@Composable () -> Unit)? = null,
    bottomContent: (@Composable () -> Unit)? = null,
    backgroundColor: Color = Theme.colorScheme.background.surface,
    errorState: ErrorState? = null,
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .then(modifier)
    ) {
        when (errorState) {
            is ErrorState.NoInternet -> ErrorView(
                image = painterResource(Res.drawable.img_no_internet),
                title = stringResource(Res.string.no_internet_title),
                description = stringResource(Res.string.no_internet_content),
                onRetry = onRetry ?: {}
            )

            else -> {
                Scaffold(
                    topBar = { topBar?.invoke() },
                    overlays = overlays ?: {},
                    content = content
                )
            }
        }

        snackBar?.let { snackBarContent ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(start = 16.dp, end = 16.dp, top = 68.dp)
            ) { snackBarContent() }
        }

        toast?.let { toast ->
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) { toast() }
        }

        bottomContent?.let {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) { it() }
        }
    }
}