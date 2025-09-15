package net.thechance.mena.core_chat.data.di

import net.thechance.mena.core_chat.data.contacts.ContactSyncWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

actual val platformModule = module { workerOf(::ContactSyncWorker)  }