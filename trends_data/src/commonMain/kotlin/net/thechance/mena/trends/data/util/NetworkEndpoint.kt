package net.thechance.mena.trends.data.util

internal object NetworkEndpoint {
    // Endpoints
    const val TRENDS_PATH = "trends"
    const val REELS_FEED_ENDPOINT = "$TRENDS_PATH/feed"
    const val THUMBNAIL_ENDPOINT = "thumbnail"
    const val LIKE_REEL_ENDPOINT = "like"
    const val VIEW_REEL_ENDPOINT = "view"
    const val CATEGORIES_ENDPOINT = "$TRENDS_PATH/categories"
    const val PROFILE_REELS_ENDPOINT = "$TRENDS_PATH/user"
    const val IDENTITY_PATH = "identity"
    const val PROFILE_ENDPOINT = "$IDENTITY_PATH/profile"
    const val USER_INFO_ENDPOINT = "$PROFILE_ENDPOINT/me"

    // Parameters
    const val PAGE_PARAMETER = "page"
}