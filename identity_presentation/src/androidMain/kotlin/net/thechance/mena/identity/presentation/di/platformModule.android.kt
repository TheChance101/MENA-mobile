package net.thechance.mena.identity.presentation.di

import android.content.Context
import android.location.LocationManager
import net.thechance.mena.identity.presentation.util.LocationForegroundPermission
import net.thechance.mena.identity.presentation.util.PermissionManager
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal actual fun platformModule(): Module = module {
    single { get<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    single<PermissionController>(named(LOCATION_FOREGROUND)) {
        LocationForegroundPermission(context = get())
    }

    single { PermissionManager() }
}