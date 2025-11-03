package net.thechance.mena.trends.presentation.screen.user_reel.args

interface UserReelArgs {
    val realId: String
    val userReelSource: UserReelSource
}

enum class UserReelSource{
    MANAGE_MY_TRENDS,
    HOME
}