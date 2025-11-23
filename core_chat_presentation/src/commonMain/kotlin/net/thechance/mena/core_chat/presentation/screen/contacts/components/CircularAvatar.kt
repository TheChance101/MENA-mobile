package net.thechance.mena.core_chat.presentation.screen.contacts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_profile_placeholder
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
fun CircularAvatar(
    contactImageUri: String?,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color = Theme.colorScheme.background.surfaceLow),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_profile_placeholder),
            contentDescription = null,
            tint = Theme.colorScheme.primary.primary,
        )

        if (!contactImageUri.isNullOrBlank()) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = contactImageUri,
                contentScale = ContentScale.Crop,
                contentDescription = "Contact photo",
            )
        }
    }
}


