package net.thechance.mena.trends.presentation.screen.main_container

interface MainContainerEffect {
    data object NavigateToTrends: MainContainerEffect
    data object NavigateToCategoryPick: MainContainerEffect
    data object NavigateToManageTrends: MainContainerEffect
    data object NavigateToUploadReel: MainContainerEffect
    data object NavigateToUpdateCategories: MainContainerEffect
}