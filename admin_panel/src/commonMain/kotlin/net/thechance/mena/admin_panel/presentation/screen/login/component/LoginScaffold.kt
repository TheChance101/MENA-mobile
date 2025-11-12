package net.thechance.mena.admin_panel.presentation.screen.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold {
        Box {
            LoginBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) { content() }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 32.dp, top = 32.dp)
                    .widthIn(min = 328.dp)
                    .fillMaxWidth(0.3f)
            ) { SnackBarContainer(snackBarState) }
        }
    }
}

@Composable
private fun LoginBackground() {
    Image(
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(Res.drawable.login_background),
        contentDescription = stringResource(Res.string.login_background_img),
        contentScale = ContentScale.Crop
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Theme.colorScheme.background.surface.copy(alpha = 0.9f),
                        Theme.colorScheme.background.surface,
                        Theme.colorScheme.background.surface,
                    ),
                    start = Offset(0f, Float.POSITIVE_INFINITY),
                    end = Offset(0f, 0f)
                )
            )
    )
}