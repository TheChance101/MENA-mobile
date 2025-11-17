package net.thechance.mena.core_chat.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient
import org.koin.compose.koinInject
import org.koin.core.qualifier.named


@Composable
fun rememberImageLoader(
    networkClient: HttpClient = koinInject<HttpClient>(named("chat_coil_client"))
): ImageLoader{
    val context = LocalPlatformContext.current
    return remember {
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory(networkClient)) }
            .build()
    }
}


val LocalImageLoader = staticCompositionLocalOf<ImageLoader> {
    throw IllegalStateException("ImageLoader not initialized")
}
