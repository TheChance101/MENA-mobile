package net.thechance.mena.identity.data.di

import net.thechance.mena.identity.data.dataSource.local.memory.ImageCacheManager
import net.thechance.mena.identity.data.dataSource.local.storage.ImagesGalleryManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

actual val IdentityPlatformModule = module {
        single { ImageCacheManager() }
        single { ImagesGalleryManager(androidApplication()) }
}