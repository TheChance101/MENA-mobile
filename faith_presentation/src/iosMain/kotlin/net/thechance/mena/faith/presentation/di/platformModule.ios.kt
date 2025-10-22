package net.thechance.mena.faith.presentation.di

import net.thechance.mena.faith.presentation.utils.AzimuthProvider
import net.thechance.mena.faith.presentation.utils.AzimuthProviderImpl
import net.thechance.mena.faith.presentation.utils.ClipboardManager
import net.thechance.mena.faith.presentation.utils.ClipboardManagerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    singleOf(::ClipboardManagerImpl).bind<ClipboardManager>()
    singleOf(::AzimuthProviderImpl).bind<AzimuthProvider>()
}
