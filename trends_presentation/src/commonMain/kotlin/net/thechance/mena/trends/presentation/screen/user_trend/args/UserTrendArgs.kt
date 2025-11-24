package net.thechance.mena.trends.presentation.screen.user_trend.args

import net.thechance.mena.trends.presentation.navigation.Route

internal interface UserTrendArgs {
    val trendId: String
    val trendSource: Route.TrendSource
}