package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.img_no_internet
import net.thechance.mena.admin_panel.resources.no_internet_content
import net.thechance.mena.admin_panel.resources.no_internet_title
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PanelScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    snackBar: (@Composable () -> Unit)? = null,
    overlays: (ScaffoldScope.() -> Unit)? = null,
    errorState: ErrorState? = null,
    isLoading: Boolean = false,
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .then(modifier)
    ) {
        Scaffold(
            topBar = { topBar?.invoke() },
            overlays = overlays ?: {},
            content = {
                when {
                    errorState is ErrorState.NoInternet -> {
                        ErrorView(
                            image = painterResource(Res.drawable.img_no_internet),
                            title = stringResource(Res.string.no_internet_title),
                            description = stringResource(Res.string.no_internet_content),
                            onRetry = onRetry ?: {}
                        )
                    }

                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            DotsProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center),
                                dotSize = 16.dp,
                                spaceBetween = 4.dp
                            )
                        }
                    }

                    else -> {
                        content()
                    }
                }
            }
        )

        snackBar?.let { snackBarContent ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 32.dp, top = 32.dp)
                    .fillMaxWidth(0.3f)
            ) { snackBarContent() }
        }
    }
}