package net.thechance.mena.core_chat.data

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.thechance.mena.core_chat.data.contacts.ContactSyncerImpl
import net.thechance.mena.core_chat.data.contacts.backgroundTaskIdentifier
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