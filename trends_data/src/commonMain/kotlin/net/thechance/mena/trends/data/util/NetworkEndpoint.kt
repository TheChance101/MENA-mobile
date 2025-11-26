package net.thechance.mena.trends.data.util

internal object NetworkEndpoint {
    // Endpoints
    const val TRENDS_PATH = "trends"
    const val TRENDS_FEED_ENDPOINT = "$TRENDS_PATH/feed"
    const val THUMBNAIL_ENDPOINT = "thumbnail"
    const val LIKE_TREND_ENDPOINT = "like"
    const val VIEW_TREND_ENDPOINT = "view"
    const val CATEGORIES_ENDPOINT = "$TRENDS_PATH/categories"
    const val PROFILE_TRENDS_ENDPOINT = "$TRENDS_PATH/user"
    const val REFRESH_TREND_ENDPOINT = "refresh"
    const val FAVORITE_TREND_ENDPOINT = "$TRENDS_PATH/favorites"
    const val WATCH_TIME_ENDPOINT = "$TRENDS_PATH/user/watch-time"

    // Parameters
    const val PAGE_PARAMETER = "page"
}