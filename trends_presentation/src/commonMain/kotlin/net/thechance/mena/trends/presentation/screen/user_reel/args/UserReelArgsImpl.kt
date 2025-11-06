package net.thechance.mena.trends.presentation.screen.user_reel.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.trends.presentation.navigation.Route
import org.koin.core.annotation.Factory

@Factory(binds = [UserReelArgs::class])
class UserReelArgsImpl(
    savedStateHandle: SavedStateHandle
) : UserReelArgs {
    override val realId: String = savedStateHandle.toRoute<Route.ReelDetails>().reelId
    override val isFromHome: Boolean = savedStateHandle.toRoute<Route.ReelDetails>().isFromHome
    override val isFromManageTrends: Boolean =
        savedStateHandle.toRoute<Route.ReelDetails>().isFromManageTrends
}