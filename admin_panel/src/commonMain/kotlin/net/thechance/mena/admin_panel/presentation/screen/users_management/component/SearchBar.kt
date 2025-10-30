package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_search
import net.thechance.mena.admin_panel.resources.search_hint
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(48.dp),
        horizontalArrangement = Arrangement.End
    ) {
        TextField(
            value = value,
            onValueChanged = onValueChange,
            leadingIcon = painterResource(Res.drawable.ic_search),
            hint = stringResource(Res.string.search_hint),
            modifier = Modifier.fillMaxWidth(0.3f)
        )
    }
}