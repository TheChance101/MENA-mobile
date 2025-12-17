package net.thechance.mena.identity.presentation.di

import net.thechance.mena.identity.presentation.util.AppLocalizer
import net.thechance.mena.identity.presentation.util.permissions.CameraPermission
import net.thechance.mena.identity.presentation.util.permissions.GalleryPermission
import net.thechance.mena.identity.presentation.util.permissions.LocationForegroundPermission
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.Permissions
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal actual fun platformModule(): Module = module {
    single<PermissionController>(named(Permissions.LOCATION_FOREGROUND.name)) {
        LocationForegroundPermission()
    }

    single<PermissionController>(named(Permissions.GALLERY_IMAGES.name)) {
        GalleryPermission()
    }

    single<PermissionController>(named(Permissions.CAMERA.name)){
        CameraPermission()
    }

    single<AppLocalizer>(
        createdAtStart = true
    ) {
        AppLocalizer(
            settingsRepository = get()
        )
    }
}