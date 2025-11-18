package net.thechance.mena.core_chat.presentation.screen.contacts.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_circular_mena_disabled
import mena.core_chat_presentation.generated.resources.ic_circular_mena_enabled
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactUiState
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource


@Composable
fun ContactItem(
    contact: ContactUiState,
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onContactClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularAvatar(
            contactImageUri = contact.imageUri,
            size = 48.dp
        )
        Column(
            modifier = Modifier.padding(start = Theme.spacing._8).weight(1f)
        ) {
            Text(
                text = contact.displayName,
                style = Theme.typography.label.large,
                color = Theme.colorScheme.shadePrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Ltr,
            ) {

                Text(
                    text = contact.phoneNumber,
                    style = Theme.typography.label.medium,
                    color = Theme.colorScheme.shadeTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = Theme.spacing._2)
                )
            }
        }

        Icon(
            painter = painterResource(
                if (contact.isMenaUser)
                    Res.drawable.ic_circular_mena_enabled
                else
                    Res.drawable.ic_circular_mena_disabled
            ),
            contentDescription = null,
            modifier = Modifier.padding(start = Theme.spacing._8).size(24.dp),
            tint = Color.Unspecified
        )
    }
}