package net.thechance.mena.faith.presentation.util

import org.koin.dsl.module

val iosModule = module {
    single<ClipboardManager> { 
        ClipboardManager() 
    }
}