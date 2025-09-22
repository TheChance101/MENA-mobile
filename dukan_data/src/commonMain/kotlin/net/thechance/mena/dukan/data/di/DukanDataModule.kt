package net.thechance.mena.dukan.data.di

import org.koin.dsl.module

val dukanDataModule = module {
    includes(dukanRepositoryModule)
}