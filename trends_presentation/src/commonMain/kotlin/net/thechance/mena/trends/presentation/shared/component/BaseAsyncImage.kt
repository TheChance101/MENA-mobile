package net.thechance.mena.trends.presentation.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.network.HttpException
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest

@Composable
fun BaseAsyncImage(
    url: String,
    contentDescription: String?,
    contentScale: ContentScale,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    callback: () -> String
){

    var currentUrl by remember { mutableStateOf(url) }

    val context = LocalPlatformContext.current

    val networkHeaders = NetworkHeaders.Builder()
        .set("X-ACCESS-DEVICE", "mobile")
        .build()

    val imageRequest = ImageRequest
        .Builder(context)
        .httpHeaders(networkHeaders)
        .data(currentUrl)
        .build()

    AsyncImage(
        model = imageRequest,
        onError = { error ->
            val throwable = error.result.throwable
            if (throwable is HttpException){
                if (throwable.response.code == 403){
                    val refreshedUrl = callback()
                    currentUrl = refreshedUrl
                }
            }
        },
        alignment = alignment,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
    )
}