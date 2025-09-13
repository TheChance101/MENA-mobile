package net.thechance.mena.dukan.presentation.screen.CreateDukan.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SaveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Theme.radius.md),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = 13.dp),
        isEnabled = isEnabled,
        isLoading = isLoading,
        containerColor = Theme.colorScheme.primary.primary,
        disabledContainerColor = Theme.colorScheme.disabled,
        contentColor = Theme.colorScheme.primary.onPrimary,
        disabledContentColor = Theme.colorScheme.textDisabled,
        borderStroke = null
    ) { color ->
        MenaText("Save", color = color, style = Theme.typography.label.medium)
    }
}

@Preview
@Composable
private fun SaveButtonPreview() {
    MenaTheme {
        SaveButton(onClick = { })
    }
}

@Preview
@Composable
private fun SaveButtonDisabledPreview() {
    MenaTheme {
        SaveButton(
            onClick = { },
            isEnabled = false
        )
    }
}