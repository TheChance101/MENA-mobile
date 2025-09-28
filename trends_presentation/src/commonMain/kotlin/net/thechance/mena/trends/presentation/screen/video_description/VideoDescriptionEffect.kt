package net.thechance.mena.trends.presentation.screen.video_description

internal sealed interface VideoDescriptionEffect {

    object NavigateBack : VideoDescriptionEffect
    data class NavigateToSelectCategories(val description: String, val trendId: String) : VideoDescriptionEffect
}