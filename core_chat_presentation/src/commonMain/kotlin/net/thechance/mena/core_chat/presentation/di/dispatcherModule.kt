package net.thechance.mena.core_chat.presentation.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val dispatcherModule = module {
    single(named(CHAT_IO_DISPATCHER)) { Dispatchers.IO }
}

internal const val CHAT_IO_DISPATCHER = "IO_DISPATCHER"