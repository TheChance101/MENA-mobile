package net.thechance.mena.dukan.presentation.screen.main.components.editorPickDukanSection

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewEditorPickDukanItemsListPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukans
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditorPickDukanItemsSection(
    state: MainScreenUiState,
    pager: Pager<Int, MainScreenUiState.EditorPickDukanUiState>,
    onDukanClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state.editorPickDukanState,
    ) {
        when (it) {
            MainScreenUiState.EditorPickDukanStatus.LOADING -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = Theme.spacing._8),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    contentPadding = PaddingValues(
                        horizontal = Theme.spacing._16,
                        vertical = Theme.spacing._8
                    ),
                ) {
                    items(8) {
                        LoadingEditorPickDukanItem()
                    }
                }
            }

            MainScreenUiState.EditorPickDukanStatus.LOADED -> {
                EditorPickDukanItemsList(
                    dukans = state.editorPickDukans,
                    pager = pager,
                    onDukanClick = onDukanClick,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun EditorPickDukanItemsList(
    dukans: PagingData<MainScreenUiState.EditorPickDukanUiState>,
    pager: Pager<Int, MainScreenUiState.EditorPickDukanUiState>,
    onDukanClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()

    lazyListState.LoadMoreOnScroll(pager)

    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        modifier = modifier.padding(horizontal = Theme.spacing._16)
    ) {
        items(
            items = dukans.items,
            key = { it.id },
            contentType = { "EditorPickDukanItem" }
        ) { dukan ->
            EditorPickDukanItem(
                dukanName = dukan.name,
                dukanImage = dukan.imageUrl,
                onClick = { onDukanClick(dukan.id) }
            )
        }
    }
}

@Preview
@Composable
private fun EditorPickDukanItemsListPreview() {
    MenaTheme {
        EditorPickDukanItemsList(
            dukans = PagingData(items = fakeDukans()),
            pager = Pager(
                config = PagingConfig(),
                pagingSourceFactory = { PreviewEditorPickDukanItemsListPagingSource }
            ),
            onDukanClick = {}
        )
    }
}
