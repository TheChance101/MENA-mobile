package net.thechance.mena.dukan.presentation.di

import org.koin.dsl.module

val dukanPresentationModule = module {
    includes(dukanViewModelModule)
}