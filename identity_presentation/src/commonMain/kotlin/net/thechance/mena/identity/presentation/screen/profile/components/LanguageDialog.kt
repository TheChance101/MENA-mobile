package net.thechance.mena.identity.presentation.screen.profile.components

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
import mena.identity_presentation.generated.resources.profile_language
import mena.identity_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.util.AppLanguage
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScaffoldScope.LanguageDialog(
    appLanguages: List<AppLanguage>,
    isVisible: Boolean,
    currentAppLanguage: AppLanguage,
    selectedAppLanguage: AppLanguage,
    onDismissRequest: () -> Unit,
    onConfirmLanguageSelection: () -> Unit,
    onLanguageChanged: (AppLanguage) -> Unit,
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
                        text = stringResource(Res.string.profile_language),
                        color = Theme.colorScheme.shadePrimary,
                        style = Theme.typography.title.small,
                    )
                }
                items(appLanguages, key = { it.iso }) {
                    LanguageOptionItem(
                        isSelected = it == selectedAppLanguage,
                        selectedAppLanguage = it,
                        onClick = {
                            onLanguageChanged(it)
                        })
                }
                item {
                    PrimaryButton(
                        text = stringResource(Res.string.save),
                        isEnabled = selectedAppLanguage != currentAppLanguage,
                        onClick = { onConfirmLanguageSelection() },
                        modifier = Modifier.padding(top = 20.dp).fillMaxWidth()
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun LanguageDialogPreview() {
    MenaTheme {
        Scaffold(overlays = {
            dialog(true) {
                LanguageDialog(
                    isVisible = it,
                    onDismissRequest = {},
                    appLanguages = listOf(AppLanguage.ENGLISH, AppLanguage.ARABIC),
                    onConfirmLanguageSelection = {},
                    currentAppLanguage = AppLanguage.ENGLISH,
                    selectedAppLanguage = AppLanguage.ENGLISH,
                    onLanguageChanged = {}
                )
            }
        }, content = {})
    }
}
