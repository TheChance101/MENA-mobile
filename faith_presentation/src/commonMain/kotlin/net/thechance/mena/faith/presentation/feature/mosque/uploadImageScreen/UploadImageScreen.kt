package net.thechance.mena.faith.presentation.feature.mosque.uploadImageScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.MosqueImageCropView
import net.thechance.mena.faith.presentation.navigation.LocalNavController

@Composable
fun UploadImageScreen(
    viewModel: UploadImageViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    MosqueImageCropView(
        aspectRatio = 16f / 9f,
        selectedImage = state.selectedImage,
        onImageCrop = { image ->
            viewModel.onImageCrop(image)
        }
    )

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is UploadImageEffect.NavigateBack -> {
                // TODO: This is where we would pass the cropped image back to the previous screen.
            }
        }
    }
}