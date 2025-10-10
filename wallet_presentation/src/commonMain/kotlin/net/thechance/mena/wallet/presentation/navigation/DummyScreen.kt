package net.thechance.mena.wallet.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun DummyScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Theme.colorScheme.background.surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "This is a placeholder for \"$title\" screen",
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.primary.primary
        )
    }
}
