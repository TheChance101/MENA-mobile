package net.thechance.mena.core_chat.data.contacts

import com.bilalazzam.contacts_provider.Contact
import com.bilalazzam.contacts_provider.ContactField.FIRST_NAME
import com.bilalazzam.contacts_provider.ContactField.ID
import com.bilalazzam.contacts_provider.ContactField.LAST_NAME
import com.bilalazzam.contacts_provider.ContactField.PHONE_NUMBERS
import com.bilalazzam.contacts_provider.ContactsProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.cinterop.ExperimentalForeignApi
import net.thechance.mena.core_chat.data.contacts.source.remote.ContactSyncer
import net.thechance.mena.core_chat.data.network.ApiConstants.SYNC_CONTACTS_ENDPOINT
import net.thechance.mena.core_chat.data.shared.dto.BaseResponseDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.BackgroundTasks.BGProcessingTaskRequest
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.dateByAddingTimeInterval
import kotlin.collections.chunked
import kotlin.getValue

const val backgroundTaskIdentifier = "net.thechance.mena.contactsync"

class ContactSyncerImpl : ContactSyncer, KoinComponent {
    private val client: HttpClient by inject()
    private val contactsProvider: ContactsProvider by inject()
    override suspend fun sync() {
        val contacts = getDeviceContacts()
        scheduleBackgroundTask()
        if (contacts.isEmpty()) {
            cancelBackgroundTask()
            return
        }
        val chunkSize = 500

        val chunks = contacts.chunked(chunkSize)

        for (chunk in chunks) {
            sendContactsToServer(chunk)
        }
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

    private suspend fun sendContactsToServer(contacts: List<Contact>) {
        client.post(SYNC_CONTACTS_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(contacts.toListOfContactCreationRequestDto())
        }.body<BaseResponseDto<Unit>>()
    }

    private suspend fun getDeviceContacts(): List<Contact> {
        return contactsProvider.getAllContacts(
            fields = setOf(ID, FIRST_NAME, LAST_NAME, PHONE_NUMBERS)
        )
    }
}