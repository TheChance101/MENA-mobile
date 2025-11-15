package net.thechance.mena.admin_panel.presentation.screen.dukan_managements.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.component.SearchBar
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.dukans
import net.thechance.mena.admin_panel.resources.search_hint
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun DukanManagementHeader(
    dukansNumbers: Int,
    onQueryChange: (String) -> Unit,
    onClearQueryClicked: () -> Unit,
    query: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(start = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        DukansCount(
            dukansNumbers = dukansNumbers.toString(),
            modifier = Modifier.align(Alignment.CenterStart)
        )
        SearchBar(
            value = query,
            hint = stringResource(Res.string.search_hint),
            onValueChange = onQueryChange,
            onClearQueryClicked = onClearQueryClicked,
            modifier = Modifier
                .widthIn(max = 400.dp)
                .align(Alignment.CenterEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun DukansCount(
    dukansNumbers: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.dukans),
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.title.small,
        )
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(CircleShape)
                .background(color = Theme.colorScheme.background.surfaceLow)
                .padding(vertical = 5.dp, horizontal = 8.dp)
        ) {
            Text(
                text = dukansNumbers,
                color = Theme.colorScheme.shadePrimary,
                style = Theme.typography.label.medium,
            )
        }
    }
}
