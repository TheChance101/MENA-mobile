package net.thechance.mena.dukan.presentation.screen.createProduct

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.navOptions
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add
import mena.dukan_presentation.generated.resources.add_product
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.price
import mena.dukan_presentation.generated.resources.price_after_discount
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.createProduct.component.DescriptionSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.ImageSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.PriceSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.ProductImageCropScreen
import net.thechance.mena.dukan.presentation.screen.createProduct.component.ProductNameSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.ShelfSection
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCreateProductInterfaceListener
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductEffect
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductUiState
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateProductScreen(
    viewModel: CreateProductViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(effects = viewModel.effect) { effect ->
        when (effect) {
            CreateProductEffect.NavigateBack -> navController.navigateUp()
            CreateProductEffect.NavigateToManageDukanProducts -> {
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

    CreateProductContent(
        state = state,
        interactionListener = viewModel
    )

    ProductImageCropScreen(
        isVisible = state.showCropImage,
        onCropImageBack = viewModel::onCroppedImage,
        onBack = viewModel::onCropImageBackClicked,
        selectedImage = state.selectedImage,
        aspectRatio = CreateProductViewModel.IMAGE_ASPECT_RATIO
    )
}

@Composable
private fun CreateProductContent(
    state: CreateProductUiState,
    interactionListener: CreateProductInteractionListener
) {
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(resource = Res.string.add_product),
                onLeadingClick = interactionListener::onBackClicked,
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_arrow),
                        tint = Theme.colorScheme.primary.primary
                    )
                }
            )
        },
        bottomBar = {
            PrimaryButton(
                text = stringResource(Res.string.add),
                onClick = interactionListener::onAddProductClicked,
                isEnabled = state.isAddButtonEnabled,
                isLoading = state.isAddButtonLoading,
                modifier = Modifier
                    .background(color = Theme.colorScheme.background.surface)
                    .padding(bottom = Theme.spacing._16)
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = Theme.spacing._16)
            )
        },
        snakeBar = {
            CreateProductSnackBar(
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
                    shelves = state.shelves,
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
                ImageSection(
                    images = state.images,
                    isUploadingImageEnabled = state.isUploadingImageEnabled,
                    isCancelImageEnabled = state.isCancelImageEnabled,
                    onUploadImageClick = interactionListener::onUploadImageClicked,
                    onCancelImageClick = interactionListener::onCancelImageClicked
                )
            }
        }
    }
}

@Composable
private fun CreateProductSnackBar(
    state: CreateProductUiState,
    interactionListener: CreateProductInteractionListener
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

@Preview
@Composable
private fun CreateProductPreview() {
    MenaTheme {
        CreateProductContent(
            state = CreateProductUiState(),
            interactionListener = PreviewCreateProductInterfaceListener
        )
    }
}