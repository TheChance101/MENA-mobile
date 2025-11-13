package net.thechance.mena.dukan.presentation.screen.createProduct.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_alert_circle
import mena.dukan_presentation.generated.resources.one_selection_for_each_product
import mena.dukan_presentation.generated.resources.shelf
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingHorizontalList
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ShelfSection(
    shelves: List<CreateProductUiState.ShelfUiState>,
    isShelvesLoading: Boolean,
    onShelfSelect: (CreateProductUiState.ShelfUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier){
        Text(
            text = stringResource(Res.string.shelf),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(horizontal = Theme.spacing._16)
        )
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = Theme.spacing._4)
                .padding(horizontal = Theme.spacing._16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._2)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_alert_circle),
                contentDescription = "shelf alert circle",
                tint = Theme.colorScheme.shadeSecondary
            )
            Text(
                text = stringResource(Res.string.one_selection_for_each_product),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }
        AnimatedContent(
            targetState = isShelvesLoading,
            transitionSpec = { fadeTransitionSpec() },
            label = "shelves loading"
        ) { isShelvesLoadingState ->
            if (isShelvesLoadingState) {
                LoadingShelves()
            } else {
                LoadedShelves(shelves, onShelfSelect)
            }
        }
    }
}

@Composable
private fun LoadingShelves() {
    LoadingHorizontalList {
        Chip(
            text = "             ",
            isSelected = false,
            isEnabled = false,
            onClick = { }
        )
    }
}

@Composable
private fun LoadedShelves(
    shelves: List<CreateProductUiState.ShelfUiState>,
    onShelfSelect: (CreateProductUiState.ShelfUiState) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .padding(top = Theme.spacing._8)
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
    ) {
        items(
            items = shelves,
            key = { it.id },
            contentType = { "Shelf" }
        ) { shelf ->
            Chip(
                text = shelf.name,
                isSelected = shelf.isSelected,
                onClick = { onShelfSelect(shelf) },
            )
        }
    }
}

@Preview
@Composable
private fun ShelvesSectionPreview() {
    MenaTheme {
        ShelfSection(
            shelves = listOf(
                CreateProductUiState.ShelfUiState(
                    id = "1",
                    name = "shelf 1",
                    isSelected = true
                ),
                CreateProductUiState.ShelfUiState(
                    id = "2",
                    name = "shelf 2",
                    isSelected = false
                ),
                CreateProductUiState.ShelfUiState(
                    id = "3",
                    name = "shelf 3",
                    isSelected = false
                ),
            ),
            onShelfSelect = {},
            isShelvesLoading = false
        )
    }
}