package net.thechance.mena.faith.data.di

import net.thechance.mena.faith.data.database.QuranDatabase
import net.thechance.mena.faith.data.database.QuranDatabaseBuilder
import net.thechance.mena.faith.data.database.getQuranDatabase
import org.koin.dsl.module

actual fun faithPlatformModule() = module {
    single<QuranDatabase> {
        getQuranDatabase(QuranDatabaseBuilder(context = get()).getBuilder())
    }
}