package net.thechance.mena.trends.presentation.navigation

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import io.github.vinceglb.filekit.coil.addPlatformFileSupport
import net.thechance.mena.trends.api.TrendsApi
import org.koin.core.annotation.Single

@Single(binds = [TrendsApi::class])
class TrendsApiImpl() : TrendsApi {

    @Composable
    override fun TabEntry() {
        SetupFileKitWithCoil()
        TrendsNavHost()
    }

    @Composable
    private fun SetupFileKitWithCoil() {
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components {
                    addPlatformFileSupport()
                }
                .build()
        }
    }
}