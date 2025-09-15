package net.thechance.mena.core_chat.data.contacts

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
import net.thechance.mena.core_chat.data.network.ApiConstants.SYNC_CONTACTS_ENDPOINT
import org.koin.core.component.KoinComponent
class ContactSyncWorker(
    appContext: Context,
    params: WorkerParameters,
    private val client: HttpClient,
    private val contactsProvider: ContactsProvider
) : CoroutineWorker(appContext, params), KoinComponent {

    override suspend fun doWork(): Result = runCatching {
        val contacts = getDeviceContacts()
        if (contacts.isEmpty()) return Result.success()
        val chunkSize = 500
        contacts.chunked(chunkSize).forEach { chunk ->
            sendContactsToServer(chunk)
        }

        Result.success()
    }.getOrElse {
        it.printStackTrace()
        Result.failure()
    }
    

    private suspend fun sendContactsToServer(contacts: List<Contact>) {
        client.post(SYNC_CONTACTS_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(contacts.toListOfContactCreationRequestDto())
        }.body<Any>()
    }

    private suspend fun getDeviceContacts(): List<Contact> {
        return contactsProvider.getAllContacts(
            fields = setOf(ID, FIRST_NAME, LAST_NAME, PHONE_NUMBERS)
        )
    }
}