package net.thechance.mena.trends.presentation.screen.category_publish

internal sealed interface CategoryPublishEffect {
    data object NavigateBack : CategoryPublishEffect
    data object NavigateToHome : CategoryPublishEffect
}