package net.thechance.mena.trends.presentation.screen.manage_my_trends

internal sealed interface ManageTrendsUiEffect {
    object NavigateBack : ManageTrendsUiEffect
    data class NavigateToTrend(val reelId: String) : ManageTrendsUiEffect
}