package net.thechance.mena.core_chat.presentation.di

import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.core_chat.presentation.api.CoreChatApiImp
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val apiModule = module {
    factoryOf(::CoreChatApiImp) bind CoreChatApi::class
}