package net.thechance.mena.dukan.presentation.screen.manageDukan.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.approved_dukan
import mena.dukan_presentation.generated.resources.dukan_approved_body
import mena.dukan_presentation.generated.resources.dukan_approved_header
import mena.dukan_presentation.generated.resources.empty_shelf
import mena.dukan_presentation.generated.resources.shelf_empty_body
import mena.dukan_presentation.generated.resources.shelf_empty_title
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.ImageWithTextContainer
import net.thechance.mena.dukan.presentation.screen.manageDukan.compnent.ProductsList
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ProductUiState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ManageDukanProducts(
    state: ManageDukanUiState,
    pager: Pager<Int, ProductUiState>,
    onProductClick: (ProductUiState) -> Unit
) {
    when {
        state.shelves.isEmpty() -> NoShelvesContent()
        state.products.items.isEmpty() -> EmptyStateContent()
        else -> ProductsList(
            products = state.products.items,
            onProductClick = onProductClick,
            pager = pager
        )
    }
}

@Composable
private fun NoShelvesContent() {
    ImageWithTextContainer(
        foregroundImageRes = Res.drawable.approved_dukan,
        header = {
            Text(
                text = stringResource(Res.string.dukan_approved_header),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                textAlign = TextAlign.Center
            )
        },
        bodyText = stringResource(Res.string.dukan_approved_body)
    )
}

@Composable
private fun EmptyStateContent() {
    ImageWithTextContainer(
        foregroundImageRes = Res.drawable.empty_shelf,
        header = {
            Text(
                text = stringResource(Res.string.shelf_empty_title),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                textAlign = TextAlign.Center
            )
        },
        bodyText = stringResource(Res.string.shelf_empty_body)
    )
}
