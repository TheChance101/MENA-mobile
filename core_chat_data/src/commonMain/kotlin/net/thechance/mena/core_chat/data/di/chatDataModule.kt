package net.thechance.mena.core_chat.data.di

import org.koin.dsl.module

val chatDataModule = module {
    includes(networkModule, repositoryModule, localDataModule, fileManagerModule)
}