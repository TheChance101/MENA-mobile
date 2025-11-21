package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_arrow_left
import mena.core_chat_presentation.generated.resources.ic_menu
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatHeader(
    chatName: String,
    onMenuClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppBar(
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._16,
            vertical = Theme.spacing._8
        ),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = null,
                tint = Theme.colorScheme.primary.primary
            )
        },
        onLeadingClick = onBackClick,
        title = chatName,
        trailingContent = {
            AppBarOptionContainer(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_menu),
                    contentDescription = null,
                    tint = Theme.colorScheme.primary.primary
                )
            }
        },
        modifier = modifier.background(Theme.colorScheme.background.surfaceLow)
    )
}

@Preview
@Composable
private fun ChatHeaderPreview() {
    MenaTheme {
        ChatHeader(chatName = "Noor Serry \uD83D\uDC31", {}, {})
    }
}