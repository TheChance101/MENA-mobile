package net.thechance.mena.dukan.presentation.screen.approvedDukan.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_add_bold
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_package_add
import mena.dukan_presentation.generated.resources.ic_pencil_edit
import mena.dukan_presentation.generated.resources.my_dukan
import mena.dukan_presentation.generated.resources.shelves
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.SnackBar
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.screen.approvedDukan.content.component.ProductCard
import net.thechance.mena.dukan.presentation.screen.approvedDukan.content.component.ProductUiState
import net.thechance.mena.dukan.presentation.screen.createDukan.content.component.CategorySelectionRow
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
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
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
                            contentDescription = "Back",
                            tint = Theme.colorScheme.shadePrimary
                        )
                    }
                )
            }

            item {
                Text(
                    text = stringResource(Res.string.shelves),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.padding(
                        horizontal = Theme.spacing._16,
                        vertical = Theme.spacing._4
                    )
                )
            }

            item {
                CategorySelectionRow(
                    availableCategories = state.categories,
                    isCategorySelected = listener.isCategorySelected(),
                    onCategorySelected = listener::onCategorySelected,
                    onCategoryDeselected = listener::onCategoryDeselected,
                    onCategoryEnabled = listener::onCategoryEnabled
                )
            }

            item {
                ProductCountRow(
                    productCount = state.productCount,
                    listener = listener
                )
            }

            item {
                // Empty state or shelf content
                if (state.shelves.isEmpty()) {
                    EmptyStateContent()
                } else {
                    ProductListContent()
                }
            }
        }

        FabButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Theme.spacing._16, bottom = Theme.spacing._24),
            onClick = listener::onAddProductClicked,
            painter = painterResource(Res.drawable.ic_add_bold)
        )
    }

    SnackBar(
        snackBarUiState = SnackBarUiState(
            snackBarType = SnackBarType.ERROR,
            message = "Failed to load shelves"
        ),
        isVisible = state.showSnackBar,
        onDismiss = listener::onDismissSnackBar
    )
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
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(Theme.radius.lg))
                .background(Theme.colorScheme.background.surfaceLow),
            contentAlignment = Alignment.Center
        ) {}

        Spacer(modifier = Modifier.height(Theme.spacing._16))

        Text(
            text = "This shelf is empty",
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Theme.spacing._8))

        Text(
            text = "start adding your products to shelf by click on add button above!",
            style = Theme.typography.body.medium,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProductListContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12)
    ) {
        // Sample products - in real app, these would come from the state
        val sampleProducts = listOf(
            ProductUiState(
                id = "1",
                name = "Sample Product 1",
                price = "$29.99",
                category = "Electronics"
            ),
            ProductUiState(
                id = "2",
                name = "Sample Product 2",
                price = "$15.50",
                category = "Clothing"
            ),
            ProductUiState(
                id = "3",
                name = "Sample Product 3",
                price = "$45.00",
                category = "Home & Garden"
            )
        )

        sampleProducts.forEach { product ->
            ProductCard(
                product = product,
                onClick = { /* TODO: Handle product click */ }
            )
        }
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
        // Product count text
        Text(
            text = "$productCount products",
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
                    contentDescription = "Add Product",
                    tint = Theme.colorScheme.shadePrimary
                )
            }

            AppBarOptionContainer(
                containerContentPadding = PaddingValues(start = 2.dp),
                onClick = { listener.onEditShelfClicked() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_pencil_edit),
                    contentDescription = "Edit Shelf",
                    tint = Theme.colorScheme.shadePrimary
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
