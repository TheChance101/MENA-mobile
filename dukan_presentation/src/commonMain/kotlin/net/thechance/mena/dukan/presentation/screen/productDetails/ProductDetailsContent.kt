package net.thechance.mena.dukan.presentation.screen.productDetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.screen.productDetails.components.AddToCartSection
import net.thechance.mena.dukan.presentation.screen.productDetails.components.ProductDetailsAppBar
import net.thechance.mena.dukan.presentation.screen.productDetails.components.ProductDetailsImagesSection
import net.thechance.mena.dukan.presentation.screen.productDetails.components.ProductDetailsInfoSection
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewProductDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeProductDetails
import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductDetailsContent(
    state: ProductDetailsUiState,
    listener: ProductDetailsInteractionListener
) {
    OnSystemBackPressed(listener::onBackClicked)

    Scaffold(
        topBar = {
            ProductDetailsAppBar(
                state = state,
                listener = listener
            )
        },
        bottomBar = {
           AnimatedVisibility(state.isLoading.not(),
               enter = slideInVertically { it },
               exit = slideOutVertically { -it }
               ) {
               AddToCartSection(
                   onAddToCartClick = { listener.onAddToCartClicked(productId = state.product.id) },
                   onPlusClick = { listener.onPlusClicked(state.product.id) },
                   onMinusClick = { listener.onMinusClicked(productId = state.product.id) },
                   state = state
               )
           }
        },
        snakeBar = {
            state.snackBarState?.let { snackBarUiState ->
                SnackBar(
                    snackBarUiState = snackBarUiState,
                    onDismiss = listener::onDismissSnackBar
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.spacing._16),
        ) {
            ProductDetailsImagesSection(
                allImages = state.product.images,
                selectedImageUrl = state.selectedImageUrl,
                onSecondaryImageClick = listener::onSecondaryImageClicked,
                isLoading = state.isLoading
            )

            ProductDetailsInfoSection(
                state = state.product,
                isLoading = state.isLoading
            )
        }
    }
}

@Preview
@Composable
private fun ProductDetailsContentPreview() {
    MenaTheme {
        ProductDetailsContent(
            state = fakeProductDetails,
            listener = PreviewProductDetailsInteractionListener
        )
    }
}

@Preview
@Composable
private fun ProductDetailsContentErrorPreview() {
    MenaTheme {
        ProductDetailsContent(
            state = fakeProductDetails.copy(
                errorState = Exception("No Internet")
            ),
            listener = PreviewProductDetailsInteractionListener
        )
    }
}