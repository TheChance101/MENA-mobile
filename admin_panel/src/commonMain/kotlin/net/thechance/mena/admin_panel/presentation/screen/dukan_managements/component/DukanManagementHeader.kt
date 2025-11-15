package net.thechance.mena.admin_panel.presentation.screen.dukan_managements.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.component.DukansCounter
import net.thechance.mena.admin_panel.presentation.component.SearchBar
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.dukans_management
import net.thechance.mena.admin_panel.resources.search_hint
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
        DukansCounter(
            count = dukansNumbers,
            title = stringResource(Res.string.dukans_management),
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