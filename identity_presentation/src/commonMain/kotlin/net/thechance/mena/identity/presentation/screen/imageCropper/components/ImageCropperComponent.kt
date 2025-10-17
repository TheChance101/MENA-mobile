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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun ImageCropperComponent(
    image: Painter,
    contentDescription: String,
    onSaveButtonClicked: (imageBitmap: ImageBitmap) -> Unit,
    onUploadAnotherImageClicked: (ImageBitmap) -> Unit,
    modifier: Modifier = Modifier,
    imagCropperUiState: ImageCropperUiState = rememberImageCropState(),
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current

    Column(
        modifier = modifier
            .padding(contentPadding)
            .clipToBounds()
    ) {
        ImageCropperSection(
            image = image,
            contentDescription = contentDescription,
            scale = imagCropperUiState.state.scale,
            translation = imagCropperUiState.state.translation,
            onTransformation = imagCropperUiState::zoomBy,
            updateImageSize = imagCropperUiState::updateImageSize,
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.radius.lg))
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Spacer(Modifier.weight(1f))

        ZoomOptionsSection(
            onResetClick = imagCropperUiState::reset,
            isZoomInEnabled = imagCropperUiState.isZoomInEnabled,
            isZoomOutEnabled = imagCropperUiState.isZoomOutEnabled,
            onZoomInClicked = { imagCropperUiState.zoomIn(0.1f) },
            onZoomOutClicked = { imagCropperUiState.zoomOut(0.1f) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = Theme.spacing._12)
                .clip(RoundedCornerShape(Theme.radius.full))
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        SaveButton(
            onClick = {
                val imageBitmap = imagCropperUiState.cropToBitmap(
                    painter = image,
                    density = density,
                    layoutDirection = direction
                )

                onSaveButtonClicked(imageBitmap)
            },
            modifier = Modifier
                .padding(top = Theme.spacing._12)
                .align(Alignment.CenterHorizontally)
        )

        UploadAnotherImageButton(
            onClick = onUploadAnotherImageClicked,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 18.dp)
        )
    }
}