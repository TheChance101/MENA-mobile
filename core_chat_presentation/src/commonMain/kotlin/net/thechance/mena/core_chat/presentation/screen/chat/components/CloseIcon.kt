package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_cancel
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CloseIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(Res.drawable.ic_cancel),
        contentDescription = null,
        modifier = modifier
            .padding(start = Theme.spacing._12, top = Theme.spacing._12)
            .clip(RoundedCornerShape(Theme.radius.full))
            .clickable(onClick = onClick)
            .background(Theme.colorScheme.background.surface)
            .padding(PaddingValues(Theme.spacing._8)),
        tint = Theme.colorScheme.primary.primary
    )
}

@Composable
@Preview()
private fun Preview() {

    MenaTheme {
        CloseIcon(
            onClick = {},
            modifier = Modifier
        )
    }
}
