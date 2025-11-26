package net.thechance.mena.faith.presentation.di

import net.thechance.mena.faith.domain.mediaPlayer.QuranPlayer
import net.thechance.mena.faith.presentation.utils.AzimuthProvider
import net.thechance.mena.faith.presentation.utils.AzimuthProviderImpl
import net.thechance.mena.faith.presentation.utils.ClipboardManager
import net.thechance.mena.faith.presentation.utils.ClipboardManagerImpl
import net.thechance.mena.faith.presentation.utils.IslamicDateCalculator
import net.thechance.mena.faith.presentation.utils.IslamicDateCalculatorImpl
import net.thechance.mena.faith.presentation.utils.MapNavigator
import net.thechance.mena.faith.presentation.utils.MapNavigatorImpl
import net.thechance.mena.faith.presentation.utils.audio.QuranPlayerImpl
import net.thechance.mena.faith.presentation.utils.permission.FaithPermissionsManager
import net.thechance.mena.faith.presentation.utils.permission.IOSFaithPermissionsManagerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    singleOf(::IslamicDateCalculatorImpl).bind<IslamicDateCalculator>()
    singleOf(::ClipboardManagerImpl).bind<ClipboardManager>()
    singleOf(::AzimuthProviderImpl).bind<AzimuthProvider>()
    singleOf(::MapNavigatorImpl).bind<MapNavigator>()
    singleOf(::QuranPlayerImpl) bind QuranPlayer::class
    singleOf(::IOSFaithPermissionsManagerImpl).bind<FaithPermissionsManager>()
}
