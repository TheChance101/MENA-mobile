package net.thechance.mena.identity.presentation.screen.profile.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.profile_theme
import mena.identity_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.button.radioButton.RadioButton
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.util.AppTheme
import net.thechance.mena.identity.presentation.util.mapThemeDrawableResource
import net.thechance.mena.identity.presentation.util.mapThemeStringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun ScaffoldScope.ThemeDialog(
    appThemes: List<AppTheme>,
    isVisible: Boolean,
    currentAppTheme: AppTheme,
    selectedAppTheme: AppTheme,
    onDismissRequest: () -> Unit,
    onConfirmThemeSelection: () -> Unit,
    onThemeChanged: (AppTheme) -> Unit,

    ) {
    BasicDialog(
        isVisible = isVisible,
        onDismiss = onDismissRequest,
        onCancelClick = onDismissRequest
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier.padding(vertical = Theme.spacing._12),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._4)
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(bottom = 20.dp),
                        text = stringResource(Res.string.profile_theme),
                        color = Theme.colorScheme.shadePrimary,
                        style = Theme.typography.title.small,
                    )
                }
                items(appThemes, key = { it.name }) {
                    ThemeOptionItem(
                        isSelected = it == selectedAppTheme,
                        selectedAppTheme = it,
                        onClick = {
                            onThemeChanged(it)
                        },
                    )
                }
                item {
                    PrimaryButton(
                        text = stringResource(Res.string.save),
                        isEnabled = selectedAppTheme != currentAppTheme,
                        onClick = { onConfirmThemeSelection() },
                        modifier = Modifier.padding(top = 20.dp).fillMaxWidth().height(48.dp)
                    )
                }
            }
        }
    }
}


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
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._12)
    ) {
        Icon(
            painter = painterResource(mapThemeDrawableResource(selectedAppTheme.name)),
            contentDescription = stringResource(Res.string.profile_theme),
            modifier = Modifier.size(Theme.spacing._24),
            tint = animatedIconTint
        )
        Text(
            text = stringResource(mapThemeStringResource(selectedAppTheme.name)),
            color = animatedTextColor,
            style = Theme.typography.title.small,
            modifier = Modifier.padding(start = Theme.spacing._8).weight(1f),
        )
        RadioButton(
            isSelected = isSelected, onClick = null
        )
    }
}

@Preview
@Composable
private fun ThemeDialogPreview() {
    MenaTheme {
        Scaffold(
            overlays = {
                dialog(true) {
                    ThemeDialog(
                        appThemes = listOf(AppTheme.LIGHT, AppTheme.DARK),
                        isVisible = true,
                        currentAppTheme = AppTheme.LIGHT,
                        selectedAppTheme = AppTheme.LIGHT,
                        onDismissRequest = {},
                        onConfirmThemeSelection = { },
                    ) {}
                }
            },
            content = {})
    }
}
