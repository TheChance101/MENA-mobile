package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.delete_message
import mena.core_chat_presentation.generated.resources.ic_delete
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ActionMenuItem(
    icon: Painter,
    text: String,
    modifier: Modifier = Modifier,
    contentColor: Color = Theme.colorScheme.shadePrimary,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .padding(top = Theme.spacing._8)
            .fillMaxWidth()
            .background(
                color = Theme.colorScheme.background.surface,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .clip(RoundedCornerShape(Theme.radius.md))
            .clickable(onClick = onClick)
            .padding(Theme.spacing._12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = contentColor
        )
        Text(
            text = text,
            style = Theme.typography.label.medium,
            color = contentColor,
            modifier = Modifier.padding(start = Theme.spacing._8)
        )
    }
}

@Composable
@Preview()
private fun PreviewActionMenuItem() {

    MenaTheme {
        ActionMenuItem(
            icon = painterResource(Res.drawable.ic_delete),
            text = stringResource(Res.string.delete_message),
            contentColor = Theme.colorScheme.error
        )
    }
}
