package net.thechance.mena.core_chat.presentation.sync

import kotlinx.cinterop.ExperimentalForeignApi
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactSyncer
import platform.BackgroundTasks.BGProcessingTaskRequest
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.dateByAddingTimeInterval

const val backgroundTaskIdentifier = "net.thechance.mena.contactsync"

class ContactSyncerImpl(
    private val contactsRepository: ContactsRepository
) : ContactSyncer {

    override suspend fun sync() {
        scheduleBackgroundTask()
        runCatching { contactsRepository.syncContacts() }
            .getOrElse { cancelBackgroundTask(); throw it }
        cancelBackgroundTask()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun scheduleBackgroundTask() {
        val request = BGProcessingTaskRequest(backgroundTaskIdentifier).apply {
            earliestBeginDate = NSDate().dateByAddingTimeInterval(2 * 60.0)
            requiresNetworkConnectivity = true
            requiresExternalPower = false
        }

        try {
            BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)
        } catch (e: Exception) {
            print("Failed to schedule background task: $e")
        }
    }

    private fun cancelBackgroundTask() {
        BGTaskScheduler.sharedScheduler.cancelTaskRequestWithIdentifier(backgroundTaskIdentifier)
    }

}