package net.thechance.mena.faith.presentation.feature.mosque.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_outline_search
import mena.faith_presentation.generated.resources.search_hint
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchBarField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        hint = stringResource(Res.string.search_hint),
        leadingIcon = painterResource(Res.drawable.ic_outline_search),
        leadingIconTint = Theme.colorScheme.shadeSecondary,
        onValueChanged = onQueryChange,
        modifier = modifier.fillMaxWidth()
    )
}