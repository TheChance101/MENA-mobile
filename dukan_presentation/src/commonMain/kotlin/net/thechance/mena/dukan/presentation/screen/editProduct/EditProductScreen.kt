package net.thechance.mena.dukan.presentation.screen.editProduct

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.navOptions
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.image_1_1
import mena.dukan_presentation.generated.resources.manage_product
import mena.dukan_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.productImage.DisplayProductImage
import net.thechance.mena.dukan.presentation.component.product.productImage.DisplayProductImageUrl
import net.thechance.mena.dukan.presentation.component.product.productImage.UploadProductImage
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.component.shared.TopAppBar
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.createProduct.component.DescriptionSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.PriceSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.ProductImageCropScreen
import net.thechance.mena.dukan.presentation.screen.createProduct.component.ProductNameSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.ShelfSection
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductUiState
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
                onDeleteClick = interactionListener::onDeleteProductClicked
            )
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
                    .height(48.dp)
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
                    shelves = state.shelves.map { editShelf ->
                        CreateProductUiState.ShelfUiState(
                            id = editShelf.id,
                            name = editShelf.name,
                            isSelected = editShelf.isSelected
                        )
                    },
                    isShelvesLoading = state.isShelvesLoading,
                    onShelfSelect = { shelf ->
                        interactionListener.onShelfSelect(
                            EditProductUiState.ShelfUiState(
                                id = shelf.id,
                                name = shelf.name,
                                isSelected = shelf.isSelected
                            )
                        )
                    }
                )
            }

            item {
                PriceSection(
                    price = state.price,
                    isTextFieldEnabled = state.isTextFieldEnabled,
                    onPriceChange = interactionListener::onPriceChange
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
                EditProductImagesSection(
                    existingUrls = state.existingImageUrls,
                    newImages = state.images.map {
                        CreateProductUiState.ProductImageUi(
                            id = it.id,
                            image = it.image,
                            imageSizeInMegaByte = it.imageSizeInMegaByte,
                            imageState = it.imageState,
                            errorMessage = it.errorMessage
                        )
                    },
                    isUploadingImageEnabled = state.isUploadingImageEnabled,
                    isCancelImageEnabled = state.isCancelImageEnabled,
                    onUploadImageClick = interactionListener::onUploadImageClicked,
                    onCancelExistingUrl = interactionListener::onCancelExistingImageUrl,
                    onCancelImageClick = interactionListener::onCancelImageClicked
                )
            }
        }
    }
}

@Composable
private fun EditProductImagesSection(
    existingUrls: List<String>,
    newImages: List<CreateProductUiState.ProductImageUi>,
    isUploadingImageEnabled: Boolean,
    isCancelImageEnabled: Boolean,
    onUploadImageClick: (ImageFile) -> Unit,
    onCancelExistingUrl: (url: String) -> Unit,
    onCancelImageClick: (ImageBitmap) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val lazyListImageState = rememberLazyListState()
        var previousSize by remember { mutableStateOf(existingUrls.size + newImages.size) }

        LaunchedEffect(existingUrls.size, newImages.size) {
            val totalImages = existingUrls.size + newImages.size
            if (totalImages > previousSize) {
                val firstNewImageIndex = if (newImages.isNotEmpty()) {
                    existingUrls.size + newImages.size - 1
                } else if (existingUrls.isNotEmpty()) {
                    existingUrls.size - 1
                } else {
                    0
                }
                lazyListImageState.animateScrollToItem(firstNewImageIndex)
            }
            previousSize = totalImages
        }

        Text(
            text = stringResource(Res.string.image_1_1),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(horizontal = Theme.spacing._16)
        )

        LazyRow(
            modifier = Modifier
                .padding(bottom = Theme.spacing._32 + Theme.spacing._16 + Theme.spacing._2)
                .height(108.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            contentPadding = PaddingValues(
                start = Theme.spacing._16,
                top = Theme.spacing._4,
                end = Theme.spacing._16
            ),
            reverseLayout = true,
            state = lazyListImageState
        ) {
            itemsIndexed(
                items = existingUrls,
                key = { index, url -> "existing_url_${index}_$url" },
                contentType = { _, _ -> "Existing Product Image Url" }
            ) { _, url ->
                DisplayProductImageUrl(
                    imageUrl = url,
                    onCancelClick = onCancelExistingUrl
                )
            }

            items(
                items = newImages,
                key = { it.id },
                contentType = { "Product Images" }
            ) { image ->
                DisplayProductImage(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(easing = FastOutSlowInEasing),
                        fadeOutSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing),
                        placementSpec = tween(easing = LinearOutSlowInEasing)
                    ),
                    image = image.image,
                    imageSizeInMegaByte = image.imageSizeInMegaByte,
                    productImageState = image.imageState,
                    onCancelClick = onCancelImageClick,
                    isCancelButtonEnabled = isCancelImageEnabled,
                    errorMessage = image.errorMessage
                )
            }

            item(key = "Upload Product Image Container") {
                UploadProductImage(
                    modifier = Modifier.size(88.dp),
                    onUploadImageClick = onUploadImageClick,
                    isUploadingImageEnabled = isUploadingImageEnabled
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
                .clip(RoundedCornerShape(Theme.radius.md))
                .clickable(onClick = interactionListener::onDismissSnackBar),
            onDismiss = interactionListener::onDismissSnackBar,
            snackBarUiState = snackBarState
        )
    }
}
