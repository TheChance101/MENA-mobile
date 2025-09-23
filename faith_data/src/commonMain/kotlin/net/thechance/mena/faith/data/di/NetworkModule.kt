package net.thechance.mena.faith.data.di

import net.thechance.mena.faith.data.remote.client.NetworkClient
import org.koin.dsl.module

val remoteDataSourceModule = module {
    single { NetworkClient().provideHttpClient() }
}