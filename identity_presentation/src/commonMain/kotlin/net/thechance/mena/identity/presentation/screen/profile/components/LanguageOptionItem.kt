package net.thechance.mena.identity.presentation.screen.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import net.thechance.mena.designsystem.presentation.component.button.radioButton.RadioButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.rippleIndication
import net.thechance.mena.identity.domain.util.AppLanguage
import net.thechance.mena.identity.presentation.util.mapLanguage
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun LanguageOptionItem(
    isSelected: Boolean,
    selectedAppLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(Theme.radius.lg))
            .background(
                color = Theme.colorScheme.background.surfaceHigh,
                shape = SquircleShape(Theme.radius.lg)
            )
            .clickable(
                enabled = !isSelected,
                onClick = {
                    onClick()
                },
                indication = rippleIndication(),
                interactionSource = remember { MutableInteractionSource() })
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._12)
    ) {

        Text(
            text = stringResource(mapLanguage(selectedAppLanguage.iso)),
            color = Theme.colorScheme.primary.primary,
            style = Theme.typography.title.small,
        )
        RadioButton(
            isSelected = isSelected, onClick = null
        )
    }
}


@Preview
@Composable
private fun LanguageOptionItemPreview() {
    MenaTheme {
    LanguageOptionItem(
        isSelected = true,
        selectedAppLanguage = AppLanguage.ENGLISH,
        onClick = {}
    )
}
}
