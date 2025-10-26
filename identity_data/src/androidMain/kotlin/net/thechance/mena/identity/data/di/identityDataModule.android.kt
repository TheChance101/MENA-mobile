package net.thechance.mena.identity.data.di

import net.thechance.mena.identity.data.dataSource.local.database.ImageCacheManager
import org.koin.core.module.Module
import org.koin.dsl.module

actual val IdentityPlatformModule = module {
        single { ImageCacheManager() }
}

