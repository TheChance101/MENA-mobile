package net.thechance.mena.trends.presentation.screen.video_description

internal data class VideoDescriptionScreenState(
    val description: String = "",
    val currentNumberOfCharacters: Int = 0,
    val maxNumberOfCharacters: Int = 3000,
    val isButtonEnabled: Boolean = true,
)
