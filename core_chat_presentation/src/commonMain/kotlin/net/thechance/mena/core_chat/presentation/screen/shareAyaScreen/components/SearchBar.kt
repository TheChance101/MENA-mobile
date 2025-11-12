package net.thechance.mena.core_chat.presentation.screen.shareAyaScreen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_clear
import mena.core_chat_presentation.generated.resources.ic_search
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource


@Composable
fun SearchBar(
    value: String,
    hint: String,
    onValueChange: (String) -> Unit,
    onClearQueryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChanged = onValueChange,
        leadingIcon = painterResource(Res.drawable.ic_search),
        hint = hint,
        showTrailingDivider = false,
        leadingIconTint = Theme.colorScheme.shadeSecondary,
        trailingIcon = if (value.isNotBlank()) painterResource(Res.drawable.ic_clear) else null,
        onTrailingIconClick = onClearQueryClicked,
        modifier = modifier
    )
}