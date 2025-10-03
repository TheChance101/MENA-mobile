package net.thechance.mena.identity.data.di

import com.russhwolf.settings.Settings
import io.ktor.client.engine.cio.CIO
import net.thechance.mena.identity.data.repository.AuthenticationRepositoryImpl
import net.thechance.mena.identity.data.repository.ResetPasswordRepositoryImpl
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.domain.service.AuthorizationService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

expect val IdentityPlatformModule: Module
val identityDataModule = module {
    single { CIO.create() }
    single<AuthenticationRepository> {
        AuthenticationRepositoryImpl(
            client = get(named("IdentityClient")),
            settings = get()
        )
    }
    singleOf(::Settings)
    singleOf(::AuthorizationService)
    single(named("IdentityClient")) {
        provideHttpClient(
            engine = get(),
            baseUrl = get<String>(named("baseUrl")),
            settings = get(),
            refreshToken = { get<AuthorizationService>().refreshToken() }
        )
    }
    singleOf(::ResetPasswordRepositoryImpl) bind ResetPasswordRepository::class
}
