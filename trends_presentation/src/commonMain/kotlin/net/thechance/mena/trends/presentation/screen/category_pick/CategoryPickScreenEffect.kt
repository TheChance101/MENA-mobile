package net.thechance.mena.trends.presentation.screen.category_pick

internal sealed interface CategoryPickScreenEffect {
    data object NavigateBack : CategoryPickScreenEffect
    data object NavigateToHome : CategoryPickScreenEffect
}