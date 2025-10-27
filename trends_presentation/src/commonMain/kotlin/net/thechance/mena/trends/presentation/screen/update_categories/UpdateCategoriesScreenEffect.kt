package net.thechance.mena.trends.presentation.screen.update_categories

internal sealed interface UpdateCategoriesScreenEffect {
    data object NavigateBack : UpdateCategoriesScreenEffect
    data object NavigateToTrendsAndShowSuccess : UpdateCategoriesScreenEffect
    data object SaveFailure : UpdateCategoriesScreenEffect
}