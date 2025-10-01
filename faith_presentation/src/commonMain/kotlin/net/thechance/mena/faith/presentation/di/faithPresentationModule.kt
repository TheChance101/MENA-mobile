package net.thechance.mena.faith.presentation.di

import org.koin.dsl.module

val faithPresentationModule = module {
    includes(faithViewModelModule, platformModule())
}
