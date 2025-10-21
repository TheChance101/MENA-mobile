package net.thechance.mena.dukan.presentation.screen.createProduct

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.navOptions
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.SnackBar
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.createProduct.component.ProductImageCropScreen
import net.thechance.mena.dukan.presentation.screen.createProduct.component.descriptionSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.imageSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.priceSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.productNameSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.shelfSection
import net.thechance.mena.dukan.presentation.screen.createProduct.component.topAppBar
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCreateProductInterfaceListener
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductEffect
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductUiState
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateProductScreen(viewModel: CreateProductViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(effects = viewModel.effect) { effect ->
        when (effect) {
            CreateProductEffect.NavigateBack -> navController.navigateUp()
            CreateProductEffect.NavigateToManagementProductMyDukan -> {
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
        onBack = viewModel::onCropImageBackClick,
        selectedImage = state.selectedImage,
        aspectRatio = CreateProductViewModel.IMAGE_ASPECT_RATIO
    )

    state.snackBarUiState?.let { snackBarState ->
        SnackBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing._16)
                .padding(top = 52.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .clickable(onClick = viewModel::onDismissSnackBar),
//            isVisible = state.showSnackBar,todo delete if not needed
            onDismiss = viewModel::onDismissSnackBar,
            snackBarUiState = snackBarState
        )
    }

}

@Composable
private fun CreateProductContent(
    state: CreateProductUiState,
    interactionListener: CreateProductInteractionListener
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Theme.colorScheme.background.surface)
            .statusBarsPadding()
            .imePadding()
    ) {
        LazyColumn {
            topAppBar(onBackClick = interactionListener::onBackButton)

            productNameSection(
                productName = state.productName,
                isTextFieldEnabled = state.isTextFieldEnabled,
                onProductNameChange = interactionListener::onProductNameChange
            )

            shelfSection(
                shelves = state.shelves,
                isTextFieldEnabled = state.isTextFieldEnabled,
                onShelfSelect = interactionListener::onShelfSelect
            )

            priceSection(
                price = state.price,
                isTextFieldEnabled = state.isTextFieldEnabled,
                onPriceChange = interactionListener::onPriceChange
            )

            descriptionSection(
                description = state.description,
                isTextFieldEnabled = state.isTextFieldEnabled,
                onDescriptionChange = interactionListener::onDescriptionChange
            )

            imageSection(
                images = state.images,
                isUploadingImageEnabled = state.isUploadingImageEnabled,
                isCancelImageEnabled = state.isCancelImageEnabled,
                onUploadImageClick = interactionListener::onUploadImageClick,
                onCancelImageClick = interactionListener::onCancelImageClick,
            )

        }

        PrimaryButton(
            text = stringResource(Res.string.add),
            onClick = interactionListener::onAddProductClick,
            isEnabled = state.isAddButtonEnabled,
            isLoading = state.isAddButtonLoading,
            modifier = Modifier
                .background(color = Theme.colorScheme.background.surface)
                .padding(bottom = Theme.spacing._16)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = Theme.spacing._16)
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