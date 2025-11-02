package net.thechance.mena.admin_panel.di

import de.jensklingenberg.ktorfit.Ktorfit
import net.thechance.mena.admin_panel.data.remote.api_service.AdminAuthenticationApiService
import net.thechance.mena.admin_panel.data.remote.api_service.UserApiService
import net.thechance.mena.admin_panel.data.remote.api_service.createAdminAuthenticationApiService
import net.thechance.mena.admin_panel.data.remote.api_service.createUserApiService
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.qualifier.named

actual val platformNetworkModule: Module = module {
    single<AdminAuthenticationApiService> {
        get<Ktorfit>(named(ADMIN_PANEL_KEY)).createAdminAuthenticationApiService()
    }
    single<UserApiService> {
        get<Ktorfit>(named(ADMIN_PANEL_KEY)).createUserApiService()
    }
}