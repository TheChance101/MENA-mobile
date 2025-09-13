package net.thechance.mena.dukan.presentation.screen.CreateDukan.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UploadAnotherImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    OutlinedButton(
        text = "Upload another image",
        trailingIcon = ColorPainter(Color.Transparent),
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Theme.radius.md),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = 13.dp),
        isEnabled = isEnabled,
    )
}

@Preview
@Composable
private fun UploadAnotherImageButtonPreview() {
    MenaTheme {
        Box(
            contentAlignment = Alignment.Center
        ) {
            UploadAnotherImageButton(onClick = {})
        }
    }
}