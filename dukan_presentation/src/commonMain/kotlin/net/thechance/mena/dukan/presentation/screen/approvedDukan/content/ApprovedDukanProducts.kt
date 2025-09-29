package net.thechance.mena.dukan.presentation.screen.approvedDukan.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.presentation.component.ImageWithTextContainer
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanUiState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ApprovedDukanProducts(
    state: ApprovedDukanUiState,
    onProductClick: (Product) -> Unit
) {
    when {
        state.shelves.isEmpty() -> NoShelvesContent()
        state.products.isEmpty() -> EmptyStateContent()
        else -> ProductListContent(
            products = state.products,
            onProductClick = onProductClick
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

@Composable
private fun ProductListContent(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    // TODO: Replace with ProductCard component when ready
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        contentPadding = PaddingValues(
            start = Theme.spacing._16,
            end = Theme.spacing._16,
            top = Theme.spacing._8,
            bottom = Theme.spacing._24
        )
    ) {
        items(products) { product ->
            Column(
                modifier = Modifier
            ) {
                Text(
                    text = product.name,
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary
                )
            }
        }
    }
}
