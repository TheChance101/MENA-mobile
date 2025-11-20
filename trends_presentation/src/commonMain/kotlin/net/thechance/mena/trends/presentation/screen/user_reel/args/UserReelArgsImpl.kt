package net.thechance.mena.trends.presentation.screen.user_reel.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.trends.presentation.navigation.Route
import org.koin.core.annotation.Factory

@Factory(binds = [UserReelArgs::class])
internal class UserReelArgsImpl(
    savedStateHandle: SavedStateHandle
) : UserReelArgs {
    private val route = savedStateHandle.toRoute<Route.ReelDetails>()
    override val realId: String = route.reelId
    override val reelSource: Route.ReelSource = Route.ReelSource.valueOf(route.source)
}