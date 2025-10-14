package net.thechance.mena.identity.data.di

import io.ktor.client.engine.darwin.Darwin
import net.thechance.mena.identity.data.repository.location.GeocoderWrapper
import net.thechance.mena.identity.data.repository.location.MobileGeocoderWrapper
import net.thechance.mena.identity.data.repository.location.MobileLocationRepositoryImpl
import net.thechance.mena.identity.domain.repository.MobileLocationRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val IdentityPlatformModule = module {
    single { Darwin.create() }
    singleOf(::MobileGeocoderWrapper) bind GeocoderWrapper::class
    singleOf(::MobileLocationRepositoryImpl) bind MobileLocationRepository::class
}
