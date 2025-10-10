package net.thechance.mena.trends.presentation.screen.show_real

sealed interface TrendsUiEffect {
    data class NavigateToReelDetails(val trendId: String) : TrendsUiEffect
    data object NavigateToAddReel : TrendsUiEffect
    data object NavigateToChangeTags : TrendsUiEffect
    data object NavigateToManageMyTrends : TrendsUiEffect
}