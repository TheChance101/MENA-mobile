package net.thechance.mena.core_chat.presentation.di

import org.koin.dsl.module
import net.thechance.mena.core_chat.presentation.di.audioPlayerModule

val chatPresentationModule = module {
    includes(apiModule, navigationModule, viewModelModule, dispatcherModule, audioPlayerModule)
}