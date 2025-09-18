package net.thechance.mena.faith.data.di

import net.thechance.mena.faith.data.database.AyaDao
import net.thechance.mena.faith.data.database.QuranDatabase
import net.thechance.mena.faith.data.repository.QuranRepositoryImpl
import net.thechance.mena.faith.domain.repository.QuranRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val faithDataModule = module {
    single<AyaDao> { get<QuranDatabase>().getAyaDao() }
    singleOf(::QuranRepositoryImpl) bind QuranRepository::class

}