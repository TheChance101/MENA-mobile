package net.thechance.mena.admin_panel.di

import de.jensklingenberg.ktorfit.Ktorfit
import net.thechance.mena.admin_panel.data.remote.service.AdminPanelApiService
import net.thechance.mena.admin_panel.data.remote.service.UserApiService
import net.thechance.mena.admin_panel.data.remote.service.createAdminPanelApiService
import net.thechance.mena.admin_panel.data.remote.service.createUserApiService
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.qualifier.named

actual val platformNetworkModule: Module = module {
    single<AdminPanelApiService> {
        get<Ktorfit>(named(ADMIN_PANEL_KEY)).createAdminPanelApiService()
    }
    single<UserApiService> {
        get<Ktorfit>(named(ADMIN_PANEL_KEY)).createUserApiService()
    }
}