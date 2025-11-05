package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.profile_profile_picture_content_description
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.screen.editProfile.component.AsyncProfileImage
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileImage(
    profileImageUrl: String,
    profileImageBitmap: ImageBitmap?,
    modifier: Modifier = Modifier,
    shadowColor: Color = Color(0x0F111D2E)
) {
    Box(
        modifier = modifier
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
        if (profileImageBitmap == null) {
            AsyncProfileImage(profileImageUrl)
        } else {
            Image(
                bitmap = profileImageBitmap,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(Res.string.profile_profile_picture_content_description),
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape),
            )
        }
    }

}