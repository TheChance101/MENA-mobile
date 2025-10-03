package net.thechance.mena.faith.data.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import net.thechance.mena.faith.data.database.QuranDatabase
import net.thechance.mena.faith.data.database.QuranDatabaseBuilder
import net.thechance.mena.faith.data.database.getQuranDatabase
import org.koin.dsl.module

internal actual fun platformModule() = module {
    single<QuranDatabase> {
        getQuranDatabase(QuranDatabaseBuilder(context = get()).getBuilder())
    }.also {
        Napier.base(DebugAntilog())
    }
}