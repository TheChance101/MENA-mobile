package net.thechance.mena.designsystem.presentation.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun DialogScaffold(
    modifier: Modifier = Modifier,
    scrimColor: Color = Theme.colorScheme.primary.primary.copy(0.55f),
    parentContent: @Composable () -> Unit,
    dialogContent: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .blur(4.dp)
            .navigationBarsPadding()
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        parentContent()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(scrimColor),
        contentAlignment = Alignment.Center
    ) {
        dialogContent()
    }
}