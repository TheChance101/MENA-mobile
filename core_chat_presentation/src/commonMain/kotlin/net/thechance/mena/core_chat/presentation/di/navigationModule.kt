package net.thechance.mena.core_chat.presentation.di

import net.thechance.mena.core_chat.presentation.screen.chat.ChatArgs
import net.thechance.mena.core_chat.presentation.screen.chat.ChatArgsImpl
import net.thechance.mena.core_chat.presentation.screen.shareAyaScreen.ShareMessageArgs
import net.thechance.mena.core_chat.presentation.screen.shareAyaScreen.ShareMessageArgsImpl
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsScreenArgs
import net.thechance.mena.core_chat.presentation.screen.syncContacts.SyncContactsScreenArgsImpl
import net.thechance.mena.core_chat.presentation.utils.SettingsOpener
import net.thechance.mena.core_chat.presentation.utils.SettingsOpenerImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val navigationModule = module {
    factoryOf(::SyncContactsScreenArgsImpl) bind SyncContactsScreenArgs::class
    factoryOf(::SettingsOpenerImpl) bind SettingsOpener::class
    factoryOf(::ChatArgsImpl) bind ChatArgs::class
    factoryOf(::ShareMessageArgsImpl) bind ShareMessageArgs::class
}