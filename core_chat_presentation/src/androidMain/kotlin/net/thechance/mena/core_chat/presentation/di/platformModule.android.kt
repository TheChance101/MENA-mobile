package net.thechance.mena.core_chat.presentation.di

import net.thechance.mena.core_chat.presentation.sync.ContactSyncWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

actual val platformModule = module { workerOf(::ContactSyncWorker) }