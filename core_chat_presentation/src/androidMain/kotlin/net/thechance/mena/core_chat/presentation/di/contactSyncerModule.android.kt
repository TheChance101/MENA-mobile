package net.thechance.mena.core_chat.presentation.di

import net.thechance.mena.core_chat.presentation.screen.contacts.ContactSyncer
import net.thechance.mena.core_chat.presentation.sync.ContactSyncerImpl
import org.koin.core.scope.Scope

actual fun Scope.createContactSyncerModule(): ContactSyncer {
    return ContactSyncerImpl(get())
}

