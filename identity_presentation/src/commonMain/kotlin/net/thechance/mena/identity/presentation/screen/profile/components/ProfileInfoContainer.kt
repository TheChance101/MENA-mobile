package net.thechance.mena.identity.presentation.screen.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.screen.editProfile.component.AsyncProfileImage
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun ProfileInfoContainer(
    profilePicture: String,
    fullName: String,
    userName: String,
    modifier: Modifier = Modifier,
) {
    val shadowColor = Color(0x0F111D2E)

    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier) {
            Box(
                modifier = Modifier
                    .border(1.dp, Theme.colorScheme.stroke, CircleShape)
                    .dropShadow(
                        shape = CircleShape,
                        shadow = Shadow(
                            radius = 8.dp,
                            spread = 0.dp,
                            color = shadowColor,
                            offset = DpOffset(x = 0.dp, 4.dp)
                        )
                    )
                    .dropShadow(
                        shape = CircleShape,
                        shadow = Shadow(
                            radius = 8.dp,
                            spread = 0.dp,
                            color = shadowColor,
                            offset = DpOffset(x = 4.dp, 0.dp)
                        )
                    )
            ) {
                AsyncProfileImage(profilePicture)
            }

            Box(
                modifier = Modifier
                    .padding(end = 15.dp, bottom = 3.dp)
                    .align(Alignment.BottomEnd)
                    .size(10.dp)
                    .border(1.dp, Theme.colorScheme.stroke, CircleShape)
                    .background(Theme.colorScheme.success, CircleShape)
            )
        }
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
            profilePicture = "https://i.pinimg.com/736x/b4/d6/e5/b4d6e50449fff312606a05bce43cc4c3.jpg",
            fullName = "Mohammed Ahmed Mansour",
            userName = "@Mohammed_2025",
        )
    }
}
