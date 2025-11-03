package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_clear
import net.thechance.mena.admin_panel.resources.ic_search
import net.thechance.mena.admin_panel.resources.search_hint
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onClearQueryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChanged = onValueChange,
        leadingIcon = painterResource(Res.drawable.ic_search),
        hint = stringResource(Res.string.search_hint),
        showTrailingDivider = false,
        leadingIconTint = Theme.colorScheme.shadeSecondary,
        trailingIcon = if (value.isNotBlank()) painterResource(Res.drawable.ic_clear) else null,
        onTrailingIconClick = onClearQueryClicked,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End)
            .fillMaxWidth(0.3f)
    )
}