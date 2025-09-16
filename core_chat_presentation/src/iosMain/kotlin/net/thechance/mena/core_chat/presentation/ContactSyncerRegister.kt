package net.thechance.mena.core_chat.presentation

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.thechance.mena.core_chat.presentation.sync.ContactSyncerImpl
import net.thechance.mena.core_chat.presentation.sync.backgroundTaskIdentifier
import platform.BackgroundTasks.BGTaskScheduler

fun registerTask() {
    BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
        identifier = backgroundTaskIdentifier,
        usingQueue = null
    ) { task ->
        GlobalScope.launch {
            ContactSyncerImpl().sync()
        }
    }
}