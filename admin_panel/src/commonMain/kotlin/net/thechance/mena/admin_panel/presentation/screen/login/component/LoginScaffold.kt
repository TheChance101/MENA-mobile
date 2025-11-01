package net.thechance.mena.admin_panel.presentation.screen.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.component.SnackBarContainer
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.login_background
import net.thechance.mena.admin_panel.resources.login_background_img
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LoginScaffold(
    snackBarState: SnackBarState,
    content : @Composable () -> Unit
) {
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                Brush.linearGradient(
                    listOf(
                        Theme.colorScheme.background.surface,
                        Theme.colorScheme.background.surface,
                        Theme.colorScheme.background.surface.copy(alpha = 0.6f),
                        Theme.colorScheme.background.surface.copy(alpha = 0.2f),
                    )
                )
            )
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter),
                painter = painterResource(Res.drawable.login_background),
                contentDescription = stringResource(Res.string.login_background_img),
                contentScale = ContentScale.FillBounds
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 68.dp, top = 16.dp)
                    .fillMaxWidth(0.4f)
            ) { SnackBarContainer(snackBarState) }

            content()
        }
    }
}