package net.thechance.mena.identity.presentation.screen.profile.editProfile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.pencil_edit
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.components.ProfileImage
import net.thechance.mena.identity.presentation.components.util.menaDropShadow
import org.jetbrains.compose.resources.painterResource


@Composable
fun EditProfileImage(
    profileImageUrl: String,
    profileImageBitmap: ImageBitmap?,
    modifier: Modifier = Modifier,
    onEditClicked: () -> Unit,
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier) {
            ProfileImage(
                profileImageUrl = profileImageUrl,
                profileImageBitmap = profileImageBitmap,
                modifier = Modifier
                    .padding(bottom = Theme.spacing._16)
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomCenter)
                    .menaDropShadow(
                        shape = CircleShape,
                        shadowColor = Theme.colorScheme.primary.primary,
                        alpha = 0.2f,
                        offset = DpOffset(x = 0.dp, y = 4.dp),
                    )
                    .background(
                        Theme.colorScheme.background.surface,
                        CircleShape
                    )
                    .padding(1.dp)
                    .clip(CircleShape)
                    .background(Theme.colorScheme.primary.primary, CircleShape)
                    .clickable(onClick = onEditClicked)
                    .padding(Theme.spacing._8)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.pencil_edit),
                    contentDescription = null,
                    tint = Theme.colorScheme.primary.onPrimary
                )
            }
        }
    }
}