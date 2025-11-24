package net.thechance.mena.trends.presentation.screen.manage_my_trends

import net.thechance.mena.trends.presentation.navigation.Route

internal sealed interface ManageTrendsUiEffect {
    object NavigateBack : ManageTrendsUiEffect
    data class NavigateToTrend(
        val trendId: String,
        val trendSource: Route.TrendSource
    ) : ManageTrendsUiEffect
}