package net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.profile_theme
import mena.identity_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.util.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
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
                        modifier = Modifier.padding(top = 20.dp).fillMaxWidth()
                    )
                }
            }
        }
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
