package net.thechance.mena.trends.presentation.screen.user_reel.args

import net.thechance.mena.trends.presentation.navigation.Route

internal interface UserReelArgs {
    val realId: String
    val reelSource: Route.ReelSource
}