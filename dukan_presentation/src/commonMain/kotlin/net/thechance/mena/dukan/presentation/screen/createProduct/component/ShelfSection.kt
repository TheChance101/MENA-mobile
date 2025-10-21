package net.thechance.mena.dukan.presentation.screen.createProduct.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_alert_circle
import mena.dukan_presentation.generated.resources.one_selection_for_each_product
import mena.dukan_presentation.generated.resources.shelf
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductUiState.ShelfUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.shelfSection(
    shelves: List<ShelfUiState>,
    isTextFieldEnabled: Boolean,
    onShelfSelect: (ShelfUiState) -> Unit
) {
    item {
        Text(
            text = stringResource(Res.string.shelf),
            style = Theme.typography.title.medium,
            modifier = Modifier.padding(top = Theme.spacing._12)
                .padding(horizontal = Theme.spacing._16)

        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing._4)
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

        LazyRow(
            modifier = Modifier
                .padding(start = Theme.spacing._16, top = Theme.spacing._8)
                .fillMaxWidth()
                .height(32.dp),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        ) {
            items(
                items = shelves,
                key = { it.id },
                contentType = { "Shelf" }
            ) { shelf ->
                Chip(
                    text = shelf.name,
                    modifier = Modifier.height(32.dp),
                    isSelected = shelf.isSelected,
                    onClick = { onShelfSelect(shelf) },
                    isEnabled = isTextFieldEnabled
                )
            }
        }
    }
}