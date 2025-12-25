package net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.profile_theme
import mena.identity_presentation.generated.resources.profile_theme_system_hint
import net.thechance.mena.designsystem.presentation.component.button.radioButton.RadioButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.rippleIndication
import net.thechance.mena.identity.domain.util.AppTheme
import net.thechance.mena.identity.presentation.util.mapThemeDrawableResource
import net.thechance.mena.identity.presentation.util.mapThemeStringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sv.lib.squircleshape.SquircleShape

@Composable
fun ThemeOptionItem(
    isSelected: Boolean,
    selectedAppTheme: AppTheme,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val animatedIconTint by animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.primary.primary else Theme.colorScheme.shadeSecondary,
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colorScheme.primary.primary else Theme.colorScheme.shadeSecondary,
    )
    Row(
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
        Icon(
            painter = painterResource(mapThemeDrawableResource(selectedAppTheme.name)),
            contentDescription = stringResource(Res.string.profile_theme),
            modifier = Modifier.size(Theme.spacing._24),
            tint = animatedIconTint
        )
        if (selectedAppTheme!= AppTheme.SYSTEM) {
            Text(
                text = stringResource(mapThemeStringResource(selectedAppTheme.name)),
                color = animatedTextColor,
                style = Theme.typography.title.small,
                modifier = Modifier.padding(start = Theme.spacing._8).weight(1f),
            )
        }else{
            Column(
                Modifier.padding(start = Theme.spacing._8).weight(1f),
            ) {
                Text(
                    text = stringResource(mapThemeStringResource(selectedAppTheme.name)),
                    color = animatedTextColor,
                    style = Theme.typography.title.small,

                )
                Text(
                    text = stringResource(Res.string.profile_theme_system_hint),
                    color = Theme.colorScheme.shadeTertiary,
                    style = Theme.typography.label.extraSmall,
                )
            }
        }
        RadioButton(
            isSelected = isSelected, onClick = null
        )
    }
}