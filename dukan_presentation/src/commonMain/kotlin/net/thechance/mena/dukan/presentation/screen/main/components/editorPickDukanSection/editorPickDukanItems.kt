package net.thechance.mena.dukan.presentation.screen.main.components.editorPickDukanSection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukans
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import org.jetbrains.compose.ui.tooling.preview.Preview


fun LazyListScope.editorPickDukanItems(
    state: MainScreenUiState,
    onDukanClick: (String) -> Unit,
) {
     when(state.editorPickDukanState){
         MainScreenUiState.EditorPickDukanStatus.LOADING -> {
             item {
                     Column(
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(horizontal = Theme.spacing._16),
                         verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
                     ) {
                         repeat(8) { LoadingEditorPickDukanItem() }
                     }
             }
         }
         MainScreenUiState.EditorPickDukanStatus.LOADED -> {
             items(
                 items = state.editorPickDukans.items,
                 key = { it.id },
                 contentType = { "EditorPickDukanItem" }
             ) { dukan ->
                     EditorPickDukanItem(
                         dukanName = dukan.name,
                         dukanImage = dukan.imageUrl,
                         onClick = { onDukanClick(dukan.id) },
                         modifier = Modifier.padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._8)
                     )

             }
         }
     }
 }




@Preview()
@Composable
private fun EditorPickDukanItemsListPreview() {
    MenaTheme {
        LazyColumn {
            editorPickDukanItems(
                state = MainScreenUiState(
                    editorPickDukanState = MainScreenUiState.EditorPickDukanStatus.LOADED,
                    editorPickDukans = PagingData(items = fakeDukans())
                ),
                onDukanClick = {}
            )
        }
    }
}























