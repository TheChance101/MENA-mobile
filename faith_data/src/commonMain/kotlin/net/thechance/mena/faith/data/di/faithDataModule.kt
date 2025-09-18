package net.thechance.mena.faith.data.di

import net.thechance.mena.faith.data.database.AyaDao
import net.thechance.mena.faith.data.database.QuranDatabase
import org.koin.dsl.module

val faithDataModule = module {
    single<AyaDao> { get<QuranDatabase>().getAyaDao() }
}