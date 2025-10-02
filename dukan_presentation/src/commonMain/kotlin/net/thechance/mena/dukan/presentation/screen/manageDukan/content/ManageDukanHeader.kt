package net.thechance.mena.dukan.presentation.screen.manageDukan.content

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
import mena.dukan_presentation.generated.resources.add_product
import mena.dukan_presentation.generated.resources.edit_shelf
import mena.dukan_presentation.generated.resources.ic_package_add
import mena.dukan_presentation.generated.resources.ic_pencil_edit
import mena.dukan_presentation.generated.resources.products_count
import mena.dukan_presentation.generated.resources.shelves
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ManageDukanHeader(
    state: ManageDukanUiState,
    listener: ManageDukanInteractionListener
) {
    Column {
        Text(
            text = stringResource(Res.string.shelves),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(
                horizontal = Theme.spacing._16,
                vertical = Theme.spacing._8
            )
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = Theme.spacing._16),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
        ) {
            items(
                items = state.shelves,
                key = { item -> item.id }
            ) { item ->
                Chip(
                    text = item.name,
                    isSelected = listener.isShelfSelected(item),
                    onClick = { listener.onShelfSelected(item) },
                )
            }
        }

        ProductCountRow(
            productCount = state.totalProducts,
            listener = listener
        )
    }
}

@Composable
private fun ProductCountRow(
    productCount: Long,
    listener: ManageDukanInteractionListener
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Theme.spacing._16, end = Theme.spacing._16, top = Theme.spacing._16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.products_count, productCount),
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadeSecondary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            AppBarOptionContainer(
                modifier = Modifier.padding(end = Theme.spacing._4),
                onClick = { listener.onAddProductClicked() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_package_add),
                    contentDescription = stringResource(Res.string.add_product),
                    tint = Theme.colorScheme.shadePrimary
                )
            }

            AppBarOptionContainer(
                onClick = { listener.onEditShelfClicked() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_pencil_edit),
                    contentDescription = stringResource(Res.string.edit_shelf),
                    tint = Theme.colorScheme.shadePrimary
                )
            }
        }
    }
}
