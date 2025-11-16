package net.thechance.mena.faith.data.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import net.thechance.mena.faith.data.audio.QuranPlayerImpl
import net.thechance.mena.faith.data.database.QuranDatabase
import net.thechance.mena.faith.data.database.QuranDatabaseBuilder
import net.thechance.mena.faith.data.database.getQuranDatabase
import net.thechance.mena.faith.domain.mediaPlayer.QuranPlayer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformModule() = module {
    single<HttpClientEngine> { Darwin.create() }
    single<QuranDatabase> {
        getQuranDatabase(QuranDatabaseBuilder().getBuilder())
    }

    singleOf(::QuranPlayerImpl) bind QuranPlayer::class
}