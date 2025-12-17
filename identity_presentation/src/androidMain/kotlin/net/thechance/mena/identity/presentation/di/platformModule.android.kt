package net.thechance.mena.identity.presentation.di

import android.content.Context
import android.location.LocationManager
import net.thechance.mena.identity.presentation.util.AppLocalizer
import net.thechance.mena.identity.presentation.util.permissions.CameraPermission
import net.thechance.mena.identity.presentation.util.permissions.GalleryPermission
import net.thechance.mena.identity.presentation.util.permissions.LocationForegroundPermission
import net.thechance.mena.identity.presentation.util.PermissionManager
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.Permissions
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal actual fun platformModule(): Module = module {
    single { get<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    single<AppLocalizer>(
        createdAtStart = true
    ) {
        AppLocalizer(
            context = get(),
            settingsRepository = get()
        )
    }

    single { PermissionManager() }

    single<PermissionController>(named(Permissions.LOCATION_FOREGROUND.name)) {
        LocationForegroundPermission(context = get(), permissionManager = get())
    }

    single<PermissionController>(named(Permissions.GALLERY_IMAGES.name)) {
        GalleryPermission(context = get(), permissionManager = get())
    }

    single<PermissionController>(named(Permissions.CAMERA.name)){
        CameraPermission(context = get(), permissionManager = get())
    }
}