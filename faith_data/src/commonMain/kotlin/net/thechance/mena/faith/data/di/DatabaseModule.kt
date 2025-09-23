package net.thechance.mena.faith.data.di

import net.thechance.mena.faith.data.database.QuranDao
import net.thechance.mena.faith.data.database.QuranDatabase
import org.koin.dsl.module

val databaseModule = module {
    single<QuranDao> { get<QuranDatabase>().getAyaDao() }
}