package net.thechance.mena.identity.presentation.screen.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.close_dialog_icon
import mena.identity_presentation.generated.resources.ic_close_dialog
import mena.identity_presentation.generated.resources.profile_language
import mena.identity_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.rippleIndication
import net.thechance.mena.identity.domain.util.AppLanguage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScaffoldScope.LanguageDialog(
    appLanguages: List<AppLanguage>,
    isVisible: Boolean,
    currentAppLanguage: AppLanguage,
    onDismissRequest: () -> Unit,
    onConfirmLanguageSelection: (AppLanguage) -> Unit,

    ) {
    var selectedLanguage by rememberSaveable(isVisible, currentAppLanguage) {
        mutableStateOf(
            currentAppLanguage
        )
    }
    BasicDialog(
        isVisible = isVisible,
        onDismiss = onDismissRequest,
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
                        isSelected = it == selectedLanguage, selectedAppLanguage = it, onClick = {
                            selectedLanguage = it
                        })
                }
                item {
                    PrimaryButton(
                        text = stringResource(Res.string.save),
                        isEnabled = selectedLanguage != currentAppLanguage,
                        onClick = { onConfirmLanguageSelection(selectedLanguage) },
                        modifier = Modifier.padding(top = 20.dp).fillMaxWidth().height(48.dp)
                    )
                }
            }
            Icon(
                painter = painterResource(Res.drawable.ic_close_dialog),
                contentDescription = stringResource(Res.string.close_dialog_icon),
                modifier = Modifier.size(Theme.spacing._32)
                    .clip(CircleShape).background(Theme.colorScheme.background.surface, CircleShape)
                    .clickable(
                        onClick = { onDismissRequest() },
                        indication = rippleIndication(),
                        interactionSource = remember { MutableInteractionSource() })
                    .padding(Theme.spacing._8).align(Alignment.TopStart))
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
            )
        }
    }, content = {})
    }
}
