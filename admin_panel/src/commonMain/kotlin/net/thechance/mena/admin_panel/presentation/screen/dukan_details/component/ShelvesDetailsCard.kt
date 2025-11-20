@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.domain.entity.dukan.Product
import net.thechance.mena.admin_panel.domain.entity.dukan.Shelf
import net.thechance.mena.admin_panel.presentation.component.AdminPanelContentLoading
import net.thechance.mena.admin_panel.presentation.utils.PaginationTrigger
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.shelves
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
internal fun ShelvesDetailsCard(
    totalShelves: String,
    shelves: List<Shelf>,
    selectedShelf: String,
    onShelfClicked: (Uuid) -> Unit,
    onNextShelvesPageRequested: () -> Unit,
    onNextProductsPageRequested: () -> Unit,
    products: List<Product>,
    isProductLoading: Boolean,
    isShelvesLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.xl)
            )
    ) {

        when{
            isShelvesLoading && shelves.isEmpty() -> {
                AdminPanelContentLoading()
            }
            else -> {
                ShelfHeader(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                    totalShelves = totalShelves
                )

                Shelves(
                    modifier = Modifier.padding(top = 12.dp),
                    shelves = shelves,
                    selectedShelf = selectedShelf,
                    onShelfClicked = onShelfClicked,
                    onNextPageRequested = onNextShelvesPageRequested,
                    isShelvesLoading = isShelvesLoading
                )

                ProductsList(
                    modifier = Modifier.padding(top = 12.dp),
                    products = products,
                    onNextPageRequested = onNextProductsPageRequested,
                    isProductLoading = isProductLoading
                )
            }
        }
    }
}

@Composable
private fun ShelfHeader(
    totalShelves: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.shelves),
            style = Theme.typography.title.large,
            color = Theme.colorScheme.shadePrimary
        )
        if (totalShelves.isNotEmpty()){
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .widthIn(min = 32.dp)
                    .heightIn(min = 32.dp)
                    .background(
                        color = Theme.colorScheme.background.surface,
                        shape = CircleShape
                    )
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = totalShelves,
                    style = Theme.typography.label.medium,
                    color = Theme.colorScheme.shadePrimary
                )
            }
        }
    }
}

@Composable
private fun Shelves(
    shelves: List<Shelf>,
    selectedShelf: String,
    onShelfClicked: (Uuid) -> Unit,
    onNextPageRequested: () -> Unit,
    isShelvesLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    PaginationTrigger(
        list = shelves,
        listState = listState,
        buffer = 5,
        loadNextItems = onNextPageRequested
    )

    LazyRow(
        modifier = modifier,
        state = listState,
    ) {
        items(shelves) { shelf ->
            Chip(
                modifier = Modifier.padding(start = 8.dp),
                text = shelf.title,
                isSelected = shelf.id.toString() == selectedShelf,
                onClick = { onShelfClicked(shelf.id) }
            )
        }
        if (isShelvesLoading){
            item { DotsProgressIndicator(dotSize = 4.dp, spaceBetween = 2.dp) }
        }
    }
}