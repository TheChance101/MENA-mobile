package net.thechance.mena.dukan.data.di

import io.ktor.client.HttpClient
import net.thechance.mena.dukan.data.repository.DukanProductRepositoryImpl
import net.thechance.mena.dukan.data.repository.DukanRepositoryImpl
import net.thechance.mena.dukan.data.repository.ShelfRepositoryImpl
import net.thechance.mena.dukan.data.repository.location.GeocoderWrapper
import net.thechance.mena.dukan.data.repository.location.LocationRepositoryImpl
import net.thechance.mena.dukan.data.repository.location.MobileGeocoderWrapper
import net.thechance.mena.dukan.data.repository.util.buildClient
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.domain.repository.LocationRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val dukanRepositoryModule = module {
    single<HttpClient>(named("dukanClient")) {
        buildClient(
            authorizationService = get(),
            baseUrl = get<String>(named("baseUrl"))
        )
    }
    single<DukanRepository> { DukanRepositoryImpl(get(named("dukanClient"))) }
    single<ProductRepository> { DukanProductRepositoryImpl(get(named("dukanClient"))) }
    single<ShelfRepository> { ShelfRepositoryImpl(get(named("dukanClient"))) }
    singleOf(::MobileGeocoderWrapper) { bind<GeocoderWrapper>() }
    singleOf(::LocationRepositoryImpl) { bind<LocationRepository>() }
}