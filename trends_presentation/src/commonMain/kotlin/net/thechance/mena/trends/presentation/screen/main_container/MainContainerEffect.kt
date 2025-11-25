package net.thechance.mena.trends.presentation.screen.main_container

interface MainContainerEffect {
    data object NavigateToTrendHome: MainContainerEffect
    data object NavigateToCategoryPick: MainContainerEffect
}