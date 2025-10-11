package net.thechance.mena.dukan.presentation.screen.dukans.compnent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.DukanCard
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.stubPreviews.FakeDukanPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukans
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukanUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukansList(
    dukans: List<DukanUiState>,
    pager: Pager<Int, DukanUiState>,
    modifier: Modifier = Modifier,
    onDukanClick: (DukanUiState) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()

    lazyListState.LoadMoreOnScroll(pager)

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._16,
            vertical = Theme.spacing._8
        ),
        state = lazyListState
    ) {
        items(dukans) { dukan ->
            DukanCard(
                modifier = Modifier.animateItem(),
                dukan = dukan,
                onClick = { onDukanClick(dukan) }
            )
        }
    }
}

@Preview
@Composable
private fun DukansListPreview() {
    MenaTheme {
        DukansList(
            fakeDukans(),
            pager = Pager(
                config = PagingConfig(),
                pagingSourceFactory = { FakeDukanPagingSource() }
            ),
        )
    }
}
