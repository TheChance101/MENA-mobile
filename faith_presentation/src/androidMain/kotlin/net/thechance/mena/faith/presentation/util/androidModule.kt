package net.thechance.mena.faith.presentation.util

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single<ClipboardManager> { 
        ClipboardManager(context = androidContext()) 
    }
}