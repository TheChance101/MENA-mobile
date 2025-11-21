package net.thechance.mena.identity.presentation.screen.editProfile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.not_user_image
import mena.identity_presentation.generated.resources.pencil_edit
import mena.identity_presentation.generated.resources.profile_profile_picture_content_description
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.components.ProfileImage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.qualifier.named


@Composable
fun EditProfileImage(
    profileImageUrl: String,
    profileImageBitmap: ImageBitmap?,
    modifier: Modifier = Modifier,
    onEditClicked: () -> Unit,
) {
    val shadowColor = Color(0x0F111D2E)

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
                    .clip(CircleShape)
                    .background(Theme.colorScheme.primary.primary)
                    .clickable(onClick = onEditClicked)
                    .border(
                        width = 1.dp,
                        color = Theme.colorScheme.background.surface,
                        shape = CircleShape
                    )
                    .padding(Theme.spacing._8)
                    .dropShadow(
                        shape = CircleShape,
                        shadow = Shadow(
                            radius = 8.dp,
                            spread = 0.dp,
                            color = shadowColor,
                            offset = DpOffset(x = 0.dp, y = 4.dp)
                        )
                    )
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

@Composable
fun AsyncProfileImage(
    imageUrl: String,
    networkClient: HttpClient = koinInject<HttpClient>(named("CoilClient"))
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        imageLoader = ImageLoader.Builder(LocalPlatformContext.current)
            .components {
                add(KtorNetworkFetcherFactory(networkClient))
            }.build(),
        contentScale = ContentScale.Crop,
        contentDescription = stringResource(Res.string.profile_profile_picture_content_description),
        modifier = Modifier
            .size(88.dp)
            .clip(CircleShape)
            .background(Theme.colorScheme.background.surfaceLow),
        error = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .blur(
                            radius = 16.dp,
                            edgeTreatment = BlurredEdgeTreatment.Unbounded
                        ),
                    painter = painterResource(Res.drawable.not_user_image),
                    contentDescription = null,
                    tint = Theme.colorScheme.primary.primary
                )
            }
        },
    )
}