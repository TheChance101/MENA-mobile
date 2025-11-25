package net.thechance.mena.trends.presentation.screen.user_trend.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.trends.presentation.navigation.Route
import org.koin.core.annotation.Factory

@Factory(binds = [UserTrendArgs::class])
internal class UserTrendArgsImpl(
    savedStateHandle: SavedStateHandle
) : UserTrendArgs {
    private val route = savedStateHandle.toRoute<Route.TrendDetails>()
    override val trendId: String = route.trendId
    override val trendSource: Route.TrendSource = Route.TrendSource.valueOf(route.source)
}