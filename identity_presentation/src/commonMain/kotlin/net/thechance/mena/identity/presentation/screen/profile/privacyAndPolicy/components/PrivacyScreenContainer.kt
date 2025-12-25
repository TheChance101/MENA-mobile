package net.thechance.mena.identity.presentation.screen.profile.privacyAndPolicy.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.img_auth_background
import mena.identity_presentation.generated.resources.login_background
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
fun PrivacyScreenContainer(
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
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
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Theme.colorScheme.background.surface,
                            Theme.colorScheme.background.surface,
                            Theme.colorScheme.background.surface.copy(alpha = 0.6f),
                        )
                    )
                ),
            contentPadding = PaddingValues(
                vertical = Theme.spacing._8,
                horizontal = Theme.spacing._16
            ),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            content()
        }
    }
}
