package net.thechance.mena.faith.presentation.base.di

import org.koin.dsl.module

val faithPresentationModule = module {
    includes(viewModelModule)
}