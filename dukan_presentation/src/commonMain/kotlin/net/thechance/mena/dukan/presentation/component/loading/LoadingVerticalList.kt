package net.thechance.mena.dukan.presentation.component.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun LoadingVerticalList(
    itemCount: Int = 8,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Theme.spacing._16,
        vertical = Theme.spacing._8
    ),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(Theme.spacing._8),
    isScrollingEnabled: Boolean = false,
    content: @Composable () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Theme.spacing._8),
        verticalArrangement = verticalArrangement,
        contentPadding = contentPadding,
        userScrollEnabled = isScrollingEnabled
    ) {
        items(itemCount) { content() }
    }
}
