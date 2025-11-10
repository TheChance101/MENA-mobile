package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.network.HttpException
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import net.thechance.mena.trends.presentation.di.trendStorageAccessSecret
import net.thechance.mena.trends.presentation.shared.util.LocalImageLoader

private const val HTTP_UNAUTHORIZED_STATUS_EXCEPTION = 403

@Composable
fun BaseAsyncImage(
    url: String,
    contentDescription: String?,
    contentScale: ContentScale,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    imageCacheKey: String? = null,
    onRequestRefresh: () -> Unit
){
    val context = LocalPlatformContext.current
    val coilImageLoader = LocalImageLoader.current

    val networkHeaders = NetworkHeaders.Builder()
        .set("X-ACCESS-KEY", trendStorageAccessSecret)
        .build()

    val imageRequest = remember(url) {
        ImageRequest
            .Builder(context)
            .httpHeaders(networkHeaders)
            .data(url)
            .diskCacheKey(imageCacheKey)
            .memoryCacheKey(imageCacheKey)
            .build()
    }

    key(url){
        AsyncImage(
            model = imageRequest,
            onError = { error ->
                val throwable = error.result.throwable
                if (throwable is HttpException){
                    if (throwable.response.code == HTTP_UNAUTHORIZED_STATUS_EXCEPTION){
                        onRequestRefresh()
                    }
                }
            },
            imageLoader = coilImageLoader,
            alignment = alignment,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
        )
    }
}