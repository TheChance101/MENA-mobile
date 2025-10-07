package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun AddPhotoBottomSheetItem(
    modifier: Modifier = Modifier,
    iconRes: DrawableResource,
    titleRes: StringResource,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clickable { onClick() }
                .clip(CircleShape)
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(Theme.spacing._16)
        ) {
            Icon(
                modifier = Modifier.size(Theme.spacing._32),
                painter = painterResource(iconRes),
                tint = Theme.colorScheme.primary.primary,
                contentDescription = stringResource(titleRes),
            )
        }

        Text(
            text = stringResource(titleRes),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary,
        )
    }
}
