package net.thechance.mena.trends.presentation.shared.util

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
fun provideImageLoader(
    networkClient: HttpClient = koinInject<HttpClient>(named(DEFAULT_CLIENT_NAME))
): ImageLoader {
    val context = LocalPlatformContext.current
    return remember {
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory(networkClient)) }
            .build()
    }
}

val LocalImageLoader = staticCompositionLocalOf<ImageLoader> {
    error("ImageLoader not initialized")
}

const val DEFAULT_CLIENT_NAME = "defaultClient"

