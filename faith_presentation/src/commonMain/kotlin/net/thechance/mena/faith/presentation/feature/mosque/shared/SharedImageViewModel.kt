package net.thechance.mena.faith.presentation.feature.mosque.shared

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.attafitamim.krop.core.images.ImageSrc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class SharedImageViewModel : ViewModel() {
    private val _imageSrc = MutableStateFlow<ImageSrc?>(null)
    val imageSrc: StateFlow<ImageSrc?> = _imageSrc.asStateFlow()

    private val _croppedImage = MutableStateFlow<ImageBitmap?>(null)
    val croppedImage: StateFlow<ImageBitmap?> = _croppedImage.asStateFlow()

    fun updateImageSrc(imageSrc: ImageSrc) {
        _imageSrc.value = imageSrc
    }

    fun updateCroppedImage(imageBitmap: ImageBitmap) {
        _croppedImage.value = imageBitmap
    }

    fun clearImage() {
        _imageSrc.value = null
        _croppedImage.value = null
    }

}
