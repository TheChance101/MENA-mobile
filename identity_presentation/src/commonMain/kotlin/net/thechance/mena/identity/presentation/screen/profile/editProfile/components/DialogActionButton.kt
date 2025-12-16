package net.thechance.mena.identity.presentation.screen.profile.editProfile.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun DialogActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = Theme.colorScheme.error
) {
    TextButton(
        modifier = modifier
            .padding(vertical = Theme.spacing._24, horizontal = Theme.spacing._12),
        text = text,
        onClick = onClick,
        contentColor = contentColor
    )
}