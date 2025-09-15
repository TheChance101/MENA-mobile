package net.thechance.mena.core_chat.data.di

import net.thechance.mena.core_chat.data.contacts.ContactSyncerImpl
import net.thechance.mena.core_chat.data.contacts.source.remote.ContactSyncer
import org.koin.core.scope.Scope

actual fun Scope.createContactSyncerModule(): ContactSyncer {
    return ContactSyncerImpl()
}

