package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.img_auth_background
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun AuthScreenContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
    ) {
        Image(
            painter = painterResource(Res.drawable.img_auth_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .alpha(.3f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Theme.colorScheme.background.surface,
                            Theme.colorScheme.background.surface,
                            Theme.colorScheme.background.surface.copy(alpha = 0.6f),
                        )
                    )
                )
                .padding(vertical = Theme.spacing._24, horizontal = Theme.spacing._16),
            content = content
        )
    }
}
