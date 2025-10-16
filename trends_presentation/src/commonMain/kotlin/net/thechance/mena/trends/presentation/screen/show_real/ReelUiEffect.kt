package net.thechance.mena.trends.presentation.screen.show_real

sealed interface ReelUiEffect {
    data class NavigateToReelDetails(val trendId: String) : ReelUiEffect
    data object NavigateToAddReel : ReelUiEffect
    data object NavigateToChangeTags : ReelUiEffect
    data object NavigateToManageMyTrends : ReelUiEffect
}