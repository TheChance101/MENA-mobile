package net.thechance.mena.faith.data.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import net.thechance.mena.faith.data.database.QuranDatabase
import net.thechance.mena.faith.data.database.QuranDatabaseBuilder
import net.thechance.mena.faith.data.database.getQuranDatabase
import org.koin.dsl.module

internal actual fun platformModule() = module {
    single<HttpClientEngine> { Darwin.create() }
    single<QuranDatabase> {
        getQuranDatabase(QuranDatabaseBuilder().getBuilder())
    }
}