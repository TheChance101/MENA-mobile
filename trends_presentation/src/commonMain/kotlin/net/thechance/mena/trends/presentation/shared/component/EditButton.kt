package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.edit_button_description
import mena.trends_presentation.generated.resources.ic_edit
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun EditButton(
    modifier: Modifier = Modifier,
    isClickEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(
                enabled = isClickEnabled,
                onClick = onClick
            )
            .size(40.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = Theme.colorScheme.background.surface,
                shape = CircleShape
            )
            .background(color = Theme.colorScheme.primary.primary, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(resource = Res.drawable.ic_edit),
            contentDescription = stringResource(resource = Res.string.edit_button_description),
            modifier = Modifier.size(size = 20.dp),
            tint = Theme.colorScheme.primary.onPrimary,
        )
    }
}

@Preview
@Composable
private fun EditButtonPreview() {
    MenaTheme { EditButton {} }
}