package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_clear
import net.thechance.mena.admin_panel.resources.ic_search
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