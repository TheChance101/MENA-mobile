package net.thechance.mena.admin_panel.di

import de.jensklingenberg.ktorfit.Ktorfit
import net.thechance.mena.admin_panel.data.remote.service.ApiService
import org.koin.core.module.Module
import org.koin.dsl.module
import net.thechance.mena.admin_panel.data.remote.service.createApiService
import org.koin.core.qualifier.named

actual val platformNetworkModule: Module = module {
    single<ApiService> {
        get<Ktorfit>(named(ADMIN_PANEL_KEY)).createApiService()
    }
}