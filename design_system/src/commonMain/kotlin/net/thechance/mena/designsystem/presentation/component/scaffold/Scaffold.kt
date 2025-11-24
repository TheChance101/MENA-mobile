package net.thechance.mena.designsystem.presentation.component.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.applyIf

@Composable
fun Scaffold(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Theme.colorScheme.background.surface,
    statusBarColor: Color = backgroundColor,
    fullScreen: Boolean = false,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snakeBar: @Composable () -> Unit = {},
    overlays: ScaffoldScope .() -> Unit = {},
    content: @Composable () -> Unit,
) {
    val scope = remember { ScaffoldScopeImpl() }.apply {
        items.clear()
        overlays()
    }

    val hasBlur = scope.items.any { it.isVisible }

    Box(
        modifier = modifier
            .fillMaxSize()
            .applyIf(hasBlur) { blur(4.dp) }
            .applyIf(!fullScreen) {
                background(statusBarColor)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .navigationBarsPadding()
                    .systemBarsPadding()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.background(backgroundColor)
        ) {
            topBar()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                content()
            }
            bottomBar()
        }

        Box(
            modifier = Modifier.align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            snakeBar()
        }
    }

    scope.items.forEach {
        it.content(scope, it.isVisible)
    }
}