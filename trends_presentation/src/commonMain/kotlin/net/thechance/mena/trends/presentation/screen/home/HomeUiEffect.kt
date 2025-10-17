package net.thechance.mena.trends.presentation.screen.home

sealed interface HomeUiEffect {
    data class NavigateToReelDetails(val trendId: String) : HomeUiEffect
    data object NavigateToAddReel : HomeUiEffect
    data object NavigateToChangeTags : HomeUiEffect
    data object NavigateToManageMyTrends : HomeUiEffect
}