package net.thechance.mena.dukan.presentation.screen.cropImage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.cropImage.components.CropImageBottomContainer
import net.thechance.mena.dukan.presentation.screen.cropImage.components.ImageCropBox
import net.thechance.mena.dukan.presentation.screen.cropImage.components.ZoomControls
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropEffects
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropUiState
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropUiState.Companion.MAX_ZOOM
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropUiState.Companion.MIN_ZOOM
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ImageCropScreen(
    selectedImage: ImageSrc?,
    aspectRatio: Float,
    onImageCrop: (ImageBitmap) -> Unit,
    viewModel: ImageCropViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            is ImageCropEffects.NavigateBack -> onImageCrop(effect.image)
        }
    }

    LaunchedEffect(selectedImage) {
        selectedImage?.let {
            viewModel.onSelectImage(it)
        }
    }

    DukanImageCropContent(
        state = state.value,
        interactionListener = viewModel,
        aspectRatio = aspectRatio,
    )
}

@Composable
private fun DukanImageCropContent(
    aspectRatio: Float,
    state: ImageCropUiState,
    interactionListener: ImageCropInteractionListener,
) {
    val currentScale = state.cropper.cropState?.transform?.scale?.x ?: 1f
    Scaffold(
        bottomBar = {
            CropImageBottomContainer(
                onUploadAnotherImageClicked = interactionListener::onUploadAnotherImageClicked,
                onSaveClicked = interactionListener::onSaveClicked
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(
                top = Theme.spacing._16,
                start = Theme.spacing._16,
                end = Theme.spacing._16
            )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.cropper.cropState?.let { cropState ->
                ImageCropBox(
                    cropState = cropState,
                    aspectRatio = aspectRatio,
                    modifier = Modifier.defaultMinSize(minHeight = 400.dp)
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
            ZoomControls(
                onZoomInClicked = interactionListener::onZoomInClicked,
                onZoomOutClicked = interactionListener::onZoomOutClicked,
                onResetClicked = interactionListener::onResetClicked,
                modifier = Modifier.padding(vertical = Theme.spacing._12),
                isZoomOutEnabled = currentScale > MIN_ZOOM,
                isZoomInEnabled = currentScale < MAX_ZOOM
            )
        }
    }
}

@Preview
@Composable
private fun DukanImageCropContentPreview() {
    MenaTheme {
        DukanImageCropContent(
            aspectRatio = 1f,
            state = ImageCropUiState(),
            interactionListener = object : ImageCropInteractionListener {
                override fun onUploadAnotherImageClicked(imageSrc: ImageSrc?) {}

                override fun onZoomInClicked() {}

                override fun onZoomOutClicked() {}

                override fun onResetClicked() {}

                override fun onSaveClicked() {}

            },
        )
    }
}