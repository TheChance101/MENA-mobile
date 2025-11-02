package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.ui.graphics.ImageBitmap

internal sealed interface CreateMosqueEffect {
    object OnBackClick : CreateMosqueEffect
    data class OnEditImageClick(val image: ImageBitmap) : CreateMosqueEffect

}