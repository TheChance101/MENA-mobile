package net.thechance.mena.core_chat.presentation.di


import net.thechance.mena.core_chat.presentation.screen.contacts.ContactSyncer
import org.koin.core.scope.Scope
import org.koin.dsl.module


internal val contactSyncerModule = module {
    single { createContactSyncerModule() }
}

expect fun Scope.createContactSyncerModule(): ContactSyncer
