package net.thechance.mena.faith.data.di

import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.database.QuranDatabase
import net.thechance.mena.faith.data.repository.QuranRepositoryImpl
import net.thechance.mena.faith.domain.repository.QuranRepository
import org.koin.dsl.module

val faithDataModule = module {
    includes(platformModule())
    single<AyahDao> { get<QuranDatabase>().getAyaDao() }
    single<QuranRepository> { QuranRepositoryImpl(get())}
}