package net.thechance.mena.identity.data.di

import io.ktor.client.engine.darwin.Darwin
import net.thechance.mena.identity.data.dataSource.local.memory.ImageCacheManager
import net.thechance.mena.identity.data.dataSource.local.storage.ImagesGalleryManager
import org.koin.dsl.module

actual val IdentityPlatformModule = module {
    single { Darwin.create() }
    single { ImageCacheManager() }
    single { ImagesGalleryManager() }
}