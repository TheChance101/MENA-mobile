package net.thechance.mena.dukan.presentation.screen.editProduct

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.navOptions
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.manage_product
import mena.dukan_presentation.generated.resources.price
import mena.dukan_presentation.generated.resources.price_after_discount
import mena.dukan_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.component.shared.TopAppBar
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.createProduct.component.DescriptionSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.ImageSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.PriceSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.ProductImageCropScreen
import net.thechance.mena.dukan.presentation.screen.createProduct.component.ProductNameSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.ShelfSection
import net.thechance.mena.dukan.presentation.screen.editProduct.component.StockStatusSection
import net.thechance.mena.dukan.presentation.screen.editProduct.component.editProductDialog
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.editProduct.EditProductEffect
import net.thechance.mena.dukan.presentation.viewModel.editProduct.EditProductInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.editProduct.EditProductUiState
import net.thechance.mena.dukan.presentation.viewModel.editProduct.EditProductViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditProductScreen(
    viewModel: EditProductViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(effects = viewModel.effect) { effect ->
        when (effect) {
            EditProductEffect.NavigateBack -> navController.navigateUp()
            EditProductEffect.NavigateToManageDukanProducts -> {
                val navOptions = navOptions {
                    popUpTo(DukanRoute.ManageDukanScreenRoute) { inclusive = true }
                }
                navController.navigate(
                    route = DukanRoute.ManageDukanScreenRoute,
                    navOptions = navOptions
                )
            }
        }
    }

    EditProductContent(
        state = state,
        interactionListener = viewModel
    )

    ProductImageCropScreen(
        isVisible = state.showCropImage,
        onCropImageBack = viewModel::onCroppedImage,
        onBack = viewModel::onCropImageBackClicked,
        selectedImage = state.selectedImage,
        aspectRatio = EditProductViewModel.IMAGE_ASPECT_RATIO
    )
}

@Composable
private fun EditProductContent(
    state: EditProductUiState,
    interactionListener: EditProductInteractionListener
) {
    Scaffold(
        topBar = {
            TopAppBar(
                onBackClick = interactionListener::onBackClicked,
                title = stringResource(Res.string.manage_product),
                onDeleteClick = interactionListener::onDeleteProductClicked,
            )
        },
        overlays = {
            editProductDialog(state, interactionListener)
        },
        bottomBar = {
            PrimaryButton(
                text = stringResource(Res.string.save),
                onClick = interactionListener::onSaveProductClicked,
                isEnabled = state.isSaveButtonEnabled,
                isLoading = state.isSaveButtonLoading,
                modifier = Modifier
                    .background(color = Theme.colorScheme.background.surface)
                    .padding(bottom = Theme.spacing._16)
                    .fillMaxWidth()
                    .padding(horizontal = Theme.spacing._16)
            )
        },
        snakeBar = {
            EditProductSnackBar(
                state = state,
                interactionListener = interactionListener
            )
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._12)
        ) {
            item {
                ProductNameSection(
                    productName = state.productName,
                    isTextFieldEnabled = state.isTextFieldEnabled,
                    onProductNameChange = interactionListener::onProductNameChange
                )
            }

            item {
                ShelfSection(
                    shelves = state.shelvesForShelfSection,
                    isShelvesLoading = state.isShelvesLoading,
                    onShelfSelect = interactionListener::onShelfSelect
                )
            }

            item {
                PriceSection(
                    title = stringResource(Res.string.price),
                    price = state.price,
                    isTextFieldEnabled = state.isTextFieldEnabled,
                    onPriceChange = interactionListener::onPriceChange
                )
            }

            item {
                PriceSection(
                    title = stringResource(Res.string.price_after_discount),
                    price = state.priceAfterDiscount,
                    isTextFieldEnabled = state.isTextFieldEnabled,
                    onPriceChange = interactionListener::onPriceAfterDiscountChange
                )
            }

            item {
                DescriptionSection(
                    description = state.description,
                    isTextFieldEnabled = state.isTextFieldEnabled,
                    onDescriptionChange = interactionListener::onDescriptionChange
                )
            }

            item {
                StockStatusSection(
                    isOutOfStock = state.isOutOfStock,
                    isEnabled = state.isTextFieldEnabled,
                    onOutOfStockChange = interactionListener::onOutOfStockChange
                )
            }

            item {
                ImageSection(
                    images = state.images,
                    existingImageUrls = state.existingImageUrls,
                    isUploadingImageEnabled = state.isUploadingImageEnabled,
                    isCancelImageEnabled = state.isCancelImageEnabled,
                    onUploadImageClick = interactionListener::onUploadImageClicked,
                    onCancelImageClick = interactionListener::onCancelImageClicked,
                    onCancelImageUrlClick = interactionListener::onCancelExistingImageUrl
                )
            }
        }
    }
}

@Composable
private fun EditProductSnackBar(
    state: EditProductUiState,
    interactionListener: EditProductInteractionListener
) {
    state.snackBarUiState?.let { snackBarState ->
        SnackBar(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = interactionListener::onDismissSnackBar),
            onDismiss = interactionListener::onDismissSnackBar,
            snackBarUiState = snackBarState
        )
    }
}
