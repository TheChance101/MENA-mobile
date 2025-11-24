package net.thechance.mena.trends.presentation.screen.home

sealed interface HomeUiEffect {
    data class NavigateToTrendDetails(val trendId: String) : HomeUiEffect
    data object NavigateToAddTrend : HomeUiEffect
    data object NavigateToChangeTags : HomeUiEffect
    data object NavigateToManageMyTrends : HomeUiEffect
}