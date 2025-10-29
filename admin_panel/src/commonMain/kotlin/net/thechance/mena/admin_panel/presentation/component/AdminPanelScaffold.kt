package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.resources.img_no_internet
import net.thechance.mena.admin_panel.resources.no_internet_content
import net.thechance.mena.admin_panel.resources.no_internet_title
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AdminPanelScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    backgroundColor: Color = Theme.colorScheme.background.surface,
    errorState: ErrorState? = null,
    isLoading: Boolean = false,
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(backgroundColor)
            .then(modifier)
    ) {
        Scaffold(
            topBar = { topBar?.invoke() },
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
                            ThreeDotsLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    else -> {
                        content()
                    }
                }
            }
        )
    }
}