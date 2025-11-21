package net.thechance.mena.identity.presentation.screen.imageCropper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.flow.collectLatest
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperComponentEffect
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperComponentInteractionListener
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperComponentViewModel
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperUiState
import net.thechance.mena.identity.presentation.screen.imageCropper.utils.cropImageToByteArray
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import sv.lib.squircleshape.SquircleShape

@Composable
fun ImageCropperComponent(
    image: Painter,
    onSaveButtonClicked: (imageByteArray: ByteArray) -> Unit,
    onUploadAnotherImageClicked: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
    minScale: Float = 1f,
    maxScale: Float = 3f,
    imagCropperViewModel: ImageCropperComponentViewModel = koinViewModel {
        parametersOf(minScale, maxScale, ImageCropperUiState())
    },
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    LaunchedEffect(Unit) {
        imagCropperViewModel.effect.collectLatest { effect ->
            when (effect) {
                is ImageCropperComponentEffect.SaveImage -> {
                    onSaveButtonClicked(effect.imageByteArray)
                }

                is ImageCropperComponentEffect.UploadAnotherImage -> {
                    onUploadAnotherImageClicked(effect.imageByteArray)
                }
            }
        }
    }

    Content(
        state = imagCropperViewModel.state,
        interactionListener = imagCropperViewModel,
        image = image,
        isZoomInEnabled = imagCropperViewModel.isZoomInEnabled,
        isZoomOutEnabled = imagCropperViewModel.isZoomOutEnabled,
        contentPadding = contentPadding,
        modifier = modifier
    )
}

@Composable
private fun Content(
    state: ImageCropperUiState,
    interactionListener: ImageCropperComponentInteractionListener,
    image: Painter,
    isZoomInEnabled: Boolean,
    isZoomOutEnabled: Boolean,
    contentPadding: PaddingValues,
    modifier: Modifier
) {
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current

    Column(modifier.padding(contentPadding)) {
        ImageCropperSection(
            image = image,
            scale = state.scale,
            translation = state.translation,
            onTransformation = interactionListener::zoomBy,
            updateComponentSize = interactionListener::updateComponentSize,
            updateImageSize = interactionListener::resetAndUpdateImageSize,
            modifier = Modifier
                .clip(SquircleShape(Theme.radius.lg))
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Spacer(Modifier.weight(1f))

        ZoomOptionsSection(
            onResetClick = interactionListener::reset,
            isZoomInEnabled = isZoomInEnabled,
            isZoomOutEnabled = isZoomOutEnabled,
            onZoomInClicked = { interactionListener.zoomIn(0.1f) },
            onZoomOutClicked = { interactionListener.zoomOut(0.1f) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = Theme.spacing._12)
                .clip(RoundedCornerShape(Theme.radius.full))
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        SaveButton(
            onClick = {
                val clippedImageByteArray = cropImageToByteArray(
                    painter = image,
                    density = density,
                    layoutDirection = direction,
                    componentSize = state.componentSize,
                    drawingSize = state.imageSize.toSize(),
                    translation = state.translation,
                    scale = state.scale
                )

                interactionListener.saveImageToGallery(clippedImageByteArray)
            },
            modifier = Modifier
                .padding(top = Theme.spacing._12)
                .align(Alignment.CenterHorizontally)
        )

        UploadAnotherImageButton(
            onClick = interactionListener::onUploadAnotherImageClicked,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 18.dp)
        )
    }
}