package net.thechance.mena.admin_panel.di

import de.jensklingenberg.ktorfit.Ktorfit
import net.thechance.mena.admin_panel.data.remote.api_service.AuthenticationApiService
import net.thechance.mena.admin_panel.data.remote.api_service.DukanApiService
import net.thechance.mena.admin_panel.data.remote.api_service.DepositMoneyApiService
import net.thechance.mena.admin_panel.data.remote.api_service.PublicApiService
import net.thechance.mena.admin_panel.data.remote.api_service.UserApiService
import net.thechance.mena.admin_panel.data.remote.api_service.createAuthenticationApiService
import net.thechance.mena.admin_panel.data.remote.api_service.createDukanApiService
import net.thechance.mena.admin_panel.data.remote.api_service.createDepositMoneyApiService
import net.thechance.mena.admin_panel.data.remote.api_service.createPublicApiService

import net.thechance.mena.admin_panel.data.remote.api_service.createUserApiService
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.qualifier.named

actual val platformNetworkModule: Module = module {
    single<AuthenticationApiService> {
        get<Ktorfit>(named(ADMIN_PANEL_KEY)).createAuthenticationApiService()
    }
    single<UserApiService> {
        get<Ktorfit>(named(ADMIN_PANEL_KEY)).createUserApiService()
    }
    single<DukanApiService> {
        get<Ktorfit>(named(ADMIN_PANEL_KEY)).createDukanApiService()
    }
    single<DepositMoneyApiService> {
        get<Ktorfit>(named(ADMIN_PANEL_KEY)).createDepositMoneyApiService()
    }
    single<PublicApiService> {
        get<Ktorfit>(named(ADMIN_PANEL_KEY)).createPublicApiService()
    }
}
