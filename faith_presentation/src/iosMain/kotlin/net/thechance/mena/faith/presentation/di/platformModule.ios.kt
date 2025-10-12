package net.thechance.mena.faith.presentation.di

import net.thechance.mena.faith.presentation.qibla.AzimuthProvider
import net.thechance.mena.faith.presentation.util.ClipboardManager
import net.thechance.mena.faith.presentation.util.ClipboardManagerImp
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    singleOf(::ClipboardManagerImp).bind<ClipboardManager>()
    singleOf(::AzimuthProvider)
}
