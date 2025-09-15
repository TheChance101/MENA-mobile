package net.thechance.mena.core_chat.presentation.screen.contacts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun CircularAvatar(
    contactImageUri: String?,
    contactInitials: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                color = Theme.colorScheme.background.surfaceLow
            ),
        contentAlignment = Alignment.Center
    ) {
        if (contactImageUri != null) {
            AsyncImage(
                model = contactImageUri,
                contentDescription = "Contact photo",
            )
        } else {
            MenaText(
                text = contactInitials,
                color = Theme.colorScheme.shadePrimary,
                style = Theme.typography.title.small,
            )
        }
    }
}


