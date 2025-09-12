package net.thechance.mena.faith.data.di

import net.thechance.mena.faith.data.repository.QuranRepositorySampleImpl
import net.thechance.mena.faith.domain.repository.QuranRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val repositoryModule = module {
    singleOf(::QuranRepositorySampleImpl) bind QuranRepository::class
}
