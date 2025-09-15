package net.thechance.mena.faith.data.di

import org.koin.dsl.module

internal val repositoryModule = module {
    singleOf(::DummyQuranRepositorySampleImpl) bind QuranRepository::class
}
