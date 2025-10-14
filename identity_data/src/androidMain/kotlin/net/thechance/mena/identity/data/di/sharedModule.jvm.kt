package net.thechance.mena.identity.data.di

import net.thechance.mena.identity.data.repository.location.GeocoderWrapper
import net.thechance.mena.identity.data.repository.location.MobileGeocoderWrapper
import net.thechance.mena.identity.data.repository.location.MobileLocationRepositoryImpl
import net.thechance.mena.identity.domain.repository.MobileLocationRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val IdentityPlatformModule: Module = module {
    singleOf(::MobileGeocoderWrapper) bind GeocoderWrapper::class
    singleOf(::MobileLocationRepositoryImpl) bind MobileLocationRepository::class
}
