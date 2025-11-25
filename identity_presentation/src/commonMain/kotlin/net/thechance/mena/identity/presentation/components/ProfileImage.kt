package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
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
import mena.identity_presentation.generated.resources.profile_profile_picture_content_description
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.components.util.menaDropShadow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun ProfileImage(
    profileImageUrl: String,
    profileImageBitmap: ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(88.dp)
            .menaDropShadow(
                shape = CircleShape,
                alpha = 0.2f,
                offset = DpOffset(0.dp, 4.dp)
            )
            .menaDropShadow(
                shape = CircleShape,
                alpha = 0.2f,
                offset = DpOffset(4.dp, 0.dp)
            )
            .menaDropShadow(
                shape = CircleShape,
                alpha = 0.2f,
                offset = DpOffset(0.dp, (-4).dp)
            )
            .menaDropShadow(
                shape = CircleShape,
                alpha = 0.2f,
                offset = DpOffset((-4).dp, 0.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
                .background(Theme.colorScheme.stroke, CircleShape)
                .clip(CircleShape)
        ) {
            if (profileImageBitmap == null) {
                AsyncProfileImage(
                    imageUrl = profileImageUrl,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    bitmap = profileImageBitmap,
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(Res.string.profile_profile_picture_content_description),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@Composable
private fun AsyncProfileImage(
    imageUrl: String,
    networkClient: HttpClient = koinInject<HttpClient>(named("CoilClient")),
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        imageLoader = ImageLoader.Builder(LocalPlatformContext.current)
            .components {
                add(KtorNetworkFetcherFactory(networkClient))
            }.build(),
        contentScale = ContentScale.Crop,
        contentDescription = stringResource(Res.string.profile_profile_picture_content_description),
        error = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.not_user_image),
                    contentDescription = null,
                    tint = Theme.colorScheme.primary.primary
                )
            }
        },
        modifier = modifier
            .background(Theme.colorScheme.background.surfaceLow)
    )
}