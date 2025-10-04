package net.thechance.mena.wallet.presentation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.painter.Painter
import net.thechance.mena.designsystem.presentation.component.chip.Chip

@Composable
fun WalletChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    painter: Painter? = null,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Chip(
        text = text,
        isSelected = isSelected,
        onClick = onClick,
        painter = painter,
        isEnabled = isEnabled,
        modifier = modifier.padding(horizontal = 16.dp)
    )
}
