package net.thechance.mena.core_chat.data.di


import net.thechance.mena.core_chat.data.contacts.source.remote.ContactSyncer
import org.koin.core.scope.Scope
import org.koin.dsl.module


internal val contactSyncerModule = module {
    single { createContactSyncerModule() }
}

expect fun Scope.createContactSyncerModule(): ContactSyncer
