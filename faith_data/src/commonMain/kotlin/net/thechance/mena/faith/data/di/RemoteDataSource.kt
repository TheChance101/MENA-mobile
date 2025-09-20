package net.thechance.mena.faith.data.di

import io.ktor.client.HttpClient
import net.thechance.mena.faith.data.remote.client.HttpClientProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val remoteDataSourceModule = module {
    singleOf<HttpClient>(HttpClientProvider()::create)
}