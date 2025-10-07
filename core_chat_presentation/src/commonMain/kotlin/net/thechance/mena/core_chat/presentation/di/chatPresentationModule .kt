package net.thechance.mena.core_chat.presentation.di

import org.koin.dsl.module

val chatPresentationModule = module {
    includes(apiModule, navigationModule, viewModelModule, dispatcherModule)
}