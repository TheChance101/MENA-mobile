package net.thechance.mena.trends.data.util

internal object NetworkConstants {
    // Endpoints
    const val TRENDS_PATH = "trends"
    const val CATEGORIES_ENDPOINT = "categories"
    const val REELS_ENDPOINT = "reels"
    const val USER_STATUS_ENDPOINT = "user/categories/status"
    const val THUMBNAIL_ENDPOINT = "$TRENDS_PATH/$REELS_ENDPOINT/thumbnail"

    const val IDENTITY_PATH = "identity"
    const val PROFILE_ENDPOINT = "profile/me"

    // Parameters
    const val PAGE_PARAMETER = "page"

    //key
    const val VIDEO = "video"
    const val THUMBNAIL = "thumbnail"
    const val THUMBNAIL_MIME_TYPE = "image/jpeg"
    const val JPEG_EXTENSION = ".jpeg"
}