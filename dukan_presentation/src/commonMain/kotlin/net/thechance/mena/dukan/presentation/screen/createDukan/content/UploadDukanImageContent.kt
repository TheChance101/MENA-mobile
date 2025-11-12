package net.thechance.mena.dukan.presentation.screen.createDukan.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukan_image
import mena.dukan_presentation.generated.resources.image_size
import mena.dukan_presentation.generated.resources.image_size_description
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.cropImage.ImageCropScreen
import net.thechance.mena.dukan.presentation.screen.cropImage.components.UploadImageContainer
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UploadDukanImageContent(
    state: CreateDukanUiState,
    interactionListener: CreateDukanInteractionListener
) {

    if (state.isImageBeingCropped) {
        OnSystemBackPressed(interactionListener::onCancelCrop)
        AnimatedVisibility(state.isImageBeingCropped) {
            ImageCropScreen(
                aspectRatio = 16f / 9f,
                selectedImage = state.selectedImage,
                onImageCrop = { image ->
                    interactionListener.onImageCrop(image)
                }
            )
        }
    } else {
        UploadDukanImageSection(
            state,
            interactionListener
        )
    }
}


@Composable
private fun UploadDukanImageSection(
    state: CreateDukanUiState,
    interactionListener: CreateDukanInteractionListener
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = Theme.spacing._16)
    ) {
        Text(
            text = stringResource(Res.string.dukan_image),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
        )

        Text(
            text = stringResource(Res.string.image_size_description),
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
        )

        Text(
            text = stringResource(Res.string.image_size),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(
                top = Theme.spacing._16,
                bottom = Theme.spacing._4
            )
        )

        UploadImageContainer(
            onClick = interactionListener::onClickUploadImage,
            image = state.croppedImage,
        )
    }
}

@Preview
@Composable
private fun UploadDukanImageContentPreview() {
    MenaTheme {
        UploadDukanImageContent(
            state = CreateDukanUiState(),
            interactionListener = PreviewCreateDukanInteractionListener
        )
    }
}