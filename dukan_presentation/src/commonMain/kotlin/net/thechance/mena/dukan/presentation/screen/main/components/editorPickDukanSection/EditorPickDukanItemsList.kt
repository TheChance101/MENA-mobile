package net.thechance.mena.dukan.presentation.screen.main.components.editorPickDukanSection

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.LazyVerticalGridItems
import net.thechance.mena.dukan.presentation.component.LoadingVerticalList
import net.thechance.mena.dukan.presentation.util.getScreenHeight
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewEditorPickDukanItemsListPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukans
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditorPickDukanItemsSection(
    state: MainScreenUiState,
    pager: Pager<Int, MainScreenUiState.EditorPickDukanUiState>,
    onDukanClick: (String) -> Unit,
    isScrollingEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state.editorPickDukanState,
    ) {
        when (it) {
            MainScreenUiState.EditorPickDukanStatus.LOADING -> {
                LoadingVerticalList(
                    modifier = Modifier.fillMaxWidth().heightIn(max = getScreenHeight()),
                    isScrollingEnabled = isScrollingEnabled
                ) {
                    LoadingEditorPickDukanItem()
                }
            }

            MainScreenUiState.EditorPickDukanStatus.LOADED -> {
                EditorPickDukanItemsList(
                    dukans = state.editorPickDukans.items,
                    pager = pager,
                    onDukanClick = onDukanClick,
                    scrollingEnabled = isScrollingEnabled,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun EditorPickDukanItemsList(
    dukans: List<MainScreenUiState.EditorPickDukanUiState>,
    pager: Pager<Int, MainScreenUiState.EditorPickDukanUiState>,
    onDukanClick: (String) -> Unit,
    scrollingEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = getScreenHeight())
    ) {
        LazyVerticalGridItems(
            items = dukans,
            pager = pager,
            key = { it.id },
            contentType = { "EditorPickDukanItem" },
            modifier = modifier.padding(horizontal = Theme.spacing._16),
            userScrollEnabled = scrollingEnabled,
            contentPadding = PaddingValues(bottom = Theme.spacing._8)
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
            dukans = fakeDukans(),
            pager = Pager(
                config = PagingConfig(),
                pagingSourceFactory = { PreviewEditorPickDukanItemsListPagingSource }
            ),
            onDukanClick = {}
        )
    }
}
