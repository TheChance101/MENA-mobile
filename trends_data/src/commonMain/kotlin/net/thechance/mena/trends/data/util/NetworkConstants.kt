package net.thechance.mena.trends.data.util

internal object NetworkConstants {
    // Endpoints
    const val TRENDS_PATH = "trends"
    const val REELS_ENDPOINT = "$TRENDS_PATH/reels"
    const val PROFILE_REELS_ENDPOINT = "$TRENDS_PATH/user/reels"
    const val USER_STATUS_ENDPOINT = "$TRENDS_PATH/user/categories/status"
    const val CATEGORIES_ENDPOINT = "$TRENDS_PATH/categories"
    const val THUMBNAIL_ENDPOINT = "$REELS_ENDPOINT/thumbnail"
    const val LIKE_REEL_ENDPOINT = "$REELS_ENDPOINT/like"
    const val VIEW_REEL_ENDPOINT = "$REELS_ENDPOINT/view"
    const val FEED_ENDPOINT = "$REELS_ENDPOINT/feed"

    const val IDENTITY_PATH = "identity"
    const val PROFILE_ENDPOINT = "profile/me"

    // Parameters
    const val PAGE_PARAMETER = "page"

    //key
    const val VIDEO = "video"
    const val THUMBNAIL = "thumbnail"
    const val THUMBNAIL_MIME_TYPE = "image/jpeg"
    const val JPEG_EXTENSION = ".jpeg"
    const val USER = "user"
}