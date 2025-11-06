package net.thechance.mena.core_chat.presentation.di

import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.core_chat.presentation.api.CoreChatApiImp
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
internal val apiModule = module {
    factory<CoreChatApi> { CoreChatApiImp(get()) }
}