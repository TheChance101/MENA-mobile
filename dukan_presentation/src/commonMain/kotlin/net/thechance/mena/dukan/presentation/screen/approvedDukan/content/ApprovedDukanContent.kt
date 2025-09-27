package net.thechance.mena.dukan.presentation.screen.approvedDukan.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_product
import mena.dukan_presentation.generated.resources.add_shelf_successfully
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.edit_shelf
import mena.dukan_presentation.generated.resources.ic_add_bold
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_package_add
import mena.dukan_presentation.generated.resources.ic_pencil_edit
import mena.dukan_presentation.generated.resources.my_dukan
import mena.dukan_presentation.generated.resources.products
import mena.dukan_presentation.generated.resources.shelves
import mena.dukan_presentation.generated.resources.this_shelf_is_empty
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.presentation.component.SelectionRow
import net.thechance.mena.dukan.presentation.component.SnackBar
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ApprovedDukanContent(
    state: ApprovedDukanUiState,
    listener: ApprovedDukanInteractionListener
) {
    OnSystemBackPressed(listener::onBackButtonClicked)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .statusBarsPadding()
    ) {
        AppBar(
            title = stringResource(Res.string.my_dukan),
            onLeadingClick = listener::onBackButtonClicked,
            contentPadding = PaddingValues(
                horizontal = Theme.spacing._16,
                vertical = Theme.spacing._8
            ),
            leadingContent = {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    contentDescription = stringResource(Res.string.back_arrow),
                    tint = Theme.colorScheme.shadePrimary
                )
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp)
        ) {
            Text(
                text = stringResource(Res.string.shelves),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.padding(
                    horizontal = Theme.spacing._16,
                    vertical = Theme.spacing._8
                )
            )

            SelectionRow(
                availableItems = state.availableShelves,
                isItemSelected = listener.isShelfSelected(),
                onItemSelected = listener::onShelfSelected,
                onItemDeselected = listener::onShelfDeselected,
                onItemEnabled = listener::onShelfEnabled,
                getItemName = { it.name }
            )

            ProductCountRow(
                productCount = state.totalProducts,
                listener = listener
            )

            when {
                state.isLoadingProducts -> LoadingProductsContent()
                state.shelves.isEmpty() -> NoShelvesContent()
                state.products.isEmpty() -> EmptyStateContent()
                else -> ProductListContent(
                    products = state.products,
                    onProductClick = listener::onProductClick
                )
            }
        }

        FabButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Theme.spacing._16, bottom = Theme.spacing._24),
            onClick = listener::onAddShelfClicked,
            painter = painterResource(Res.drawable.ic_add_bold)
        )
    }

    state.snackBarMessage?.let { snackBarMessage ->
        SnackBar(
            snackBarUiState = SnackBarUiState(
                snackBarType = SnackBarType.SUCCESS,
                message = stringResource(Res.string.add_shelf_successfully)
            ),
            isVisible = true,
            onDismiss = listener::onDismissSnackBar
        )
    }
}

@Composable
private fun ProductCountRow(
    productCount: Int,
    listener: ApprovedDukanInteractionListener
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$productCount ${stringResource(Res.string.products)}",
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadeSecondary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            AppBarOptionContainer(
                containerContentPadding = PaddingValues(end = Theme.spacing._2),
                onClick = { listener.onAddProductClicked() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_package_add),
                    contentDescription = stringResource(Res.string.add_product),
                    tint = Theme.colorScheme.shadePrimary
                )
            }

            AppBarOptionContainer(
                containerContentPadding = PaddingValues(start = 2.dp),
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

@Composable
private fun NoShelvesContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Theme.spacing._32),
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
            .padding(Theme.spacing._32),
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
            .padding(horizontal = Theme.spacing._16),
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12)
    ) {
        products.forEach { product ->
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


@Preview
@Composable
private fun ApprovedDukanContentPreview() {
    MenaTheme {
        ApprovedDukanContent(
            state = ApprovedDukanUiState(),
            listener = PreviewApprovedDukanInteractionListener
        )
    }
}
