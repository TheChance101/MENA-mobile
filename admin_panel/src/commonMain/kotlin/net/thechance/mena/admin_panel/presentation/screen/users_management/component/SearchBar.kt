package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth().height(48.dp),
        horizontalArrangement = Arrangement.End
    ) {
        TextField(
            value = value,
            onValueChanged = onValueChange,
            leadingIcon = painterResource(Res.drawable.ic_search),
            hint = stringResource(Res.string.search_hint),
            modifier = Modifier.width(400.dp)
        )
    }
}