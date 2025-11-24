package net.thechance.mena.faith.data.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import net.thechance.mena.faith.data.database.QuranDatabase
import net.thechance.mena.faith.data.database.QuranDatabaseBuilder
import net.thechance.mena.faith.data.database.getQuranDatabase
import org.koin.dsl.module

internal actual fun platformModule() = module {
    single<HttpClientEngine> { CIO.create() }
    single<QuranDatabase> {
        getQuranDatabase(QuranDatabaseBuilder(context = get()).getBuilder())
    }.also {
        Napier.base(DebugAntilog())
    }
}