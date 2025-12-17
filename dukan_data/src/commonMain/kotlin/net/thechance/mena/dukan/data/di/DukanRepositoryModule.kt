package net.thechance.mena.dukan.data.di

import io.ktor.client.HttpClient
import net.thechance.mena.dukan.data.repository.CartRepositoryImpl
import net.thechance.mena.dukan.data.repository.DukanDiscoveryRepositoryImpl
import net.thechance.mena.dukan.data.repository.DukanManagementRepositoryImpl
import net.thechance.mena.dukan.data.repository.DukanProductRepositoryImpl
import net.thechance.mena.dukan.data.repository.LocationRepositoryImpl
import net.thechance.mena.dukan.data.repository.OrderRepositoryImpl
import net.thechance.mena.dukan.data.repository.SearchRepositoryImpl
import net.thechance.mena.dukan.data.repository.ShelfRepositoryImpl
import net.thechance.mena.dukan.data.util.network.DukanApi
import net.thechance.mena.dukan.data.util.network.DukanApiClient
import net.thechance.mena.dukan.data.util.network.buildDukanCoilClient
import net.thechance.mena.dukan.data.util.network.platformHttpClientEngineFactory
import net.thechance.mena.dukan.data.util.wrapper.GeocoderWrapper
import net.thechance.mena.dukan.data.util.wrapper.MobileGeocoderWrapper
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.repository.DukanDiscoveryRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.LocationRepository
import net.thechance.mena.dukan.domain.repository.OrderRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.SearchRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val dukanRepositoryModule = module {
    single<DukanApi>(named("dukanClient")) {
        DukanApiClient(
            authorizationService = get(),
            localizationService = get(),
            baseUrl = get<String>(named("baseUrl")),
            platformHttpClientEngineFactory
        )
    }

    single<HttpClient>(named("dukanCoilClient")) {
        buildDukanCoilClient()
    }

    single<DukanDiscoveryRepository> {
        DukanDiscoveryRepositoryImpl(
            client = get(named("dukanClient")),
            locationService = get(),
            authorizationService = get()
        )
    }

    single<ProductRepository> { DukanProductRepositoryImpl(get(named("dukanClient"))) }
    single<CartRepository> { CartRepositoryImpl(get(named("dukanClient"))) }
    single<ShelfRepository> { ShelfRepositoryImpl(get(named("dukanClient"))) }
    single<DukanManagementRepository> { DukanManagementRepositoryImpl(client = get(named("dukanClient"))) }
    single<CartRepository> { CartRepositoryImpl(client = get(named("dukanClient"))) }
    single<SearchRepository> { SearchRepositoryImpl(get(named("dukanClient"))) }
    single<OrderRepository> { OrderRepositoryImpl(get(named("dukanClient"))) }
    singleOf(::MobileGeocoderWrapper) { bind<GeocoderWrapper>() }
    singleOf(::LocationRepositoryImpl) { bind<LocationRepository>() }
}