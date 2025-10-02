package net.thechance.mena.identity.data.di

import com.russhwolf.settings.Settings
import io.ktor.client.engine.cio.CIO
import net.thechance.mena.identity.data.repository.AuthenticationRepositoryImpl
import net.thechance.mena.identity.data.repository.ResetPasswordRepositoryImpl
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

expect val IdentityPlatformModule: Module
val identityDataModule = module {
    single { CIO.create() }
    singleOf(::Settings)
    single {
        provideHttpClient(
            engine = get(),
            baseUrl = get<String>(named("baseUrl")),
            settings = get()
        )
    }
    singleOf(::AuthenticationRepositoryImpl) bind AuthenticationRepository::class
    singleOf(::ResetPasswordRepositoryImpl) bind ResetPasswordRepository::class
}