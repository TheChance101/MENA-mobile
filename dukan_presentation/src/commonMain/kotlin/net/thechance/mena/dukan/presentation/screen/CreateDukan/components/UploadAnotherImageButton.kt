package net.thechance.mena.dukan.presentation.screen.CreateDukan.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UploadAnotherImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Theme.radius.md),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = 13.dp),
        isEnabled = isEnabled,
        containerColor = Theme.colorScheme.stroke,
        disabledContainerColor = Theme.colorScheme.disabled,
        contentColor = Theme.colorScheme.primary.primary,
        disabledContentColor = Theme.colorScheme.textDisabled,
        borderStroke = BorderStroke(
            width = 1.dp,
            color = Theme.colorScheme.stroke
        )
    ) { color ->
        MenaText(
            "Upload another image",
            color = color,
            style = Theme.typography.label.medium
        )
    }
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