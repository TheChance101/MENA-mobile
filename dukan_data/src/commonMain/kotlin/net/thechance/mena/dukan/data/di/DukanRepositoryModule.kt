package net.thechance.mena.dukan.data.di

import net.thechance.mena.dukan.data.repository.DukanRepositoryImpl
import net.thechance.mena.dukan.domain.repository.DukanRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val dukanRepositoryModule = module {
    singleOf(::DukanRepositoryImpl) { bind<DukanRepository>() }
}