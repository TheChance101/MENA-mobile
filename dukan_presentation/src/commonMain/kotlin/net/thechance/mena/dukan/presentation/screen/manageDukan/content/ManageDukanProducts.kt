package net.thechance.mena.dukan.presentation.screen.manageDukan.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.empty_shelf
import mena.dukan_presentation.generated.resources.shelf_empty_body
import mena.dukan_presentation.generated.resources.shelf_empty_title
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.ImageWithTextContainer
import net.thechance.mena.dukan.presentation.component.productCard.LoadingProductCard
import net.thechance.mena.dukan.presentation.screen.manageDukan.compnent.ProductsList
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ProductsState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ManageDukanProducts(
    state: ManageDukanUiState,
    pager: Pager<Int, ProductUiState>,
    onProductClick: (ProductUiState) -> Unit
) {
    AnimatedContent(
        targetState = state.productState,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "ContentAnimation"
    ) { target ->
        when (target) {
            ProductsState.LOADING -> {
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
                        LoadingProductCard()
                    }
                }
            }

            ProductsState.LOADED -> ProductsList(
                products = state.products.items,
                onProductClick = onProductClick,
                pager = pager
            )

            ProductsState.EMPTY -> EmptyStateContent()

        }
    }
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
