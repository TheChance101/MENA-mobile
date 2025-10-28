package net.thechance.mena.admin_panel.di

import de.jensklingenberg.ktorfit.Ktorfit
import net.thechance.mena.admin_panel.data.remote.service.ApiService
import org.koin.core.module.Module
import org.koin.dsl.module
import net.thechance.mena.admin_panel.data.remote.service.createApiService

actual val platformNetworkModule: Module = module {
    single<ApiService> { get<Ktorfit>().createApiService() }
}