package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.ui.graphics.ImageBitmap

internal sealed interface CreateMosqueEffect {
    object NavigateBack : CreateMosqueEffect
    data class NavigateToImageEditor(val image: ImageBitmap) : CreateMosqueEffect

}