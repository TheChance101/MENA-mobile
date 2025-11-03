package net.thechance.mena.identity.presentation.screen.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun ProfileInfoContainer(
    fullName: String,
    userName: String,
    modifier: Modifier = Modifier,
) {
    val shadowColor = Color(0x0F111D2E)

    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = fullName,
            style = Theme.typography.label.medium.copy(
                shadow = androidx.compose.ui.graphics.Shadow(
                    shadowColor,
                    Offset(0f, 4f),
                    4f
                )
            ),
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Theme.spacing._8)
        )
        Text(
            text = "@$userName",
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Theme.spacing._2)
        )
    }
}


@Preview()
@Composable
fun PreviewProfileInfoContainer() {
    MenaTheme {
        ProfileInfoContainer(
            fullName = "Mohammed Ahmed Mansour",
            userName = "@Mohammed_2025",
        )
    }
}
