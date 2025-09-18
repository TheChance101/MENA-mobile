package net.thechance.mena.faith.data.di

import org.koin.dsl.module

val faithDataModule = module {
    includes(repositoryModule)
}