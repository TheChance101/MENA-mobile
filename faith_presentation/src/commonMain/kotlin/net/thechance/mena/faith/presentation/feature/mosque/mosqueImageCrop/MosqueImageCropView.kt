package net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.back
import mena.faith_presentation.generated.resources.ic_arrow_left
import mena.faith_presentation.generated.resources.mosque_image
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.MosqueImageCropUiState.Companion.MAX_ZOOM
import net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.MosqueImageCropUiState.Companion.MIN_ZOOM
import net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.component.CropImageBottomContainer
import net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.component.ImageCropBox
import net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.component.ZoomControls
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MosqueImageCropView(
    selectedImage: ImageSrc?,
    aspectRatio: Float,
    onBackClick: () -> Unit,
    onImageCrop: (ImageBitmap) -> Unit,
    viewModel: MosqueImageCropViewModel = koinViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is MosqueImageCropEffect.NavigateBack -> onImageCrop(effect.image)
        }
    }

    LaunchedEffect(selectedImage) {
        selectedImage?.let {
            viewModel.onSelectImage(it)
        }
    }

    MosqueImageCropContent(
        state = state.value,
        interactionListener = viewModel,
        aspectRatio = aspectRatio,
        onBackClick = onBackClick
    )
}

@Composable
private fun MosqueImageCropContent(
    aspectRatio: Float,
    state: MosqueImageCropUiState,
    interactionListener: MosqueImageCropInteractionListener,
    onBackClick: () -> Unit,
) {
    val currentScale = state.cropper.cropState?.transform?.scale?.x ?: 1f
    Scaffold(
        topBar = {
            MosqueImageAppBar(onBackClick = onBackClick)
        },
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

@Composable
private fun MosqueImageAppBar(
    onBackClick: () -> Unit
) {
    AppBar(
        title = stringResource(Res.string.mosque_image),
        onLeadingClick = onBackClick,
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._12,
            vertical = Theme.spacing._8
        ),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back),
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            MosqueImageCropContent(
                aspectRatio = 16f / 9f,
                state = MosqueImageCropUiState(),
                onBackClick = {},
                interactionListener = object : MosqueImageCropInteractionListener {
                    override fun onUploadAnotherImageClicked(imageSrc: ImageSrc?) {}
                    override fun onZoomInClicked() {}
                    override fun onZoomOutClicked() {}
                    override fun onResetClicked() {}
                    override fun onSaveClicked() {}

                },
            )
        }
    }
}
