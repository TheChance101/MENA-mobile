package net.thechance.mena.identity.presentation.di

import net.thechance.mena.identity.presentation.util.LocationForegroundPermission
import net.thechance.mena.identity.presentation.util.PermissionManager
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.utils.ImageCacheManager
import net.thechance.mena.identity.presentation.utils.ImageCacheManagerImpl
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal actual fun platformModule(): Module = module {
    single<PermissionController>(named(LOCATION_FOREGROUND)) {
        LocationForegroundPermission()
    }

    single { PermissionManager() }
    single <ImageCacheManager>{ ImageCacheManagerImpl() }
}