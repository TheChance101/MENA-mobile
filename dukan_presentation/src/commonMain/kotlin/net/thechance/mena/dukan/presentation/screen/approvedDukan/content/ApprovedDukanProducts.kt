package net.thechance.mena.dukan.presentation.screen.approvedDukan.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.this_shelf_is_empty
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanUiState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ApprovedDukanProducts(
    state: ApprovedDukanUiState,
    onProductClick: (Product) -> Unit
) {
    when {
        state.isLoadingProducts -> LoadingProductsContent()
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TODO: Replace with EmptyState component from design system when ready
        Text(
            text = "Create dukan request it approved now",
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = Theme.spacing._8)
        )
        Text(
            text = "Start create you shelf and products by click on create button below!",
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyStateContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TODO: Replace with EmptyState component from design system when ready
        Text(
            text = stringResource(Res.string.this_shelf_is_empty),
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingProductsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._24),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12)
    ) {
        // TODO: Replace with Loading component when ready
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        Theme.colorScheme.shadeSecondary.copy(alpha = 0.1f),
                        RoundedCornerShape(Theme.radius.lg)
                    )
            )
        }
    }
}

@Composable
private fun ProductListContent(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    // TODO: Replace with ProductCard component when ready
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12)
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
