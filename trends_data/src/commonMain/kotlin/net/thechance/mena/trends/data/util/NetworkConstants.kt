package net.thechance.mena.trends.data.util

internal object NetworkConstants {
    // Endpoints
    const val TRENDS_PATH = "trends"
    const val CATEGORIES_ENDPOINT = "categories"
    const val INTERESTS_ENDPOINT = "interests"
    const val REELS_ENDPOINT = "reels"
    const val USER_STATUS_ENDPOINT = "user/categories/status"
    const val THUMBNAIL_ENDPOINT = "$TRENDS_PATH/$REELS_ENDPOINT/thumbnail"

    // Parameters
    const val PAGE_PARAMETER = "page"

    //keys
    const val VIDEO = "video"
    const val THUMBNAIL = "thumbnail"
    const val THUMBNAIL_MIME_TYPE = "image/jpeg"
    const val JPEG_EXTENSION = ".jpeg"
}