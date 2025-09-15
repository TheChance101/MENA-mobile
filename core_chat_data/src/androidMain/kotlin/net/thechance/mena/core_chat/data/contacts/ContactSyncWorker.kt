package net.thechance.mena.core_chat.data.contacts

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.bilalazzam.contacts_provider.Contact
import com.bilalazzam.contacts_provider.ContactField.FIRST_NAME
import com.bilalazzam.contacts_provider.ContactField.ID
import com.bilalazzam.contacts_provider.ContactField.LAST_NAME
import com.bilalazzam.contacts_provider.ContactField.PHONE_NUMBERS
import com.bilalazzam.contacts_provider.ContactsPermissionDeniedException as ContactsProviderPermissionDeniedException
import com.bilalazzam.contacts_provider.ContactsProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.core_chat.data.network.ApiConstants.SYNC_CONTACTS_ENDPOINT
import net.thechance.mena.core_chat.data.shared.dto.BaseResponseDto
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
       val exception =  when(it){
            is ContactsProviderPermissionDeniedException -> ContactSyncException.PERMISSION_DENIED_EXCEPTION
            else -> ContactSyncException.NETWORK_EXCEPTION
        }
        Result.failure(
            workDataOf(
                "error" to exception.ordinal,
                "errorMessage" to (it.message ?: "Unknown error")
            )
        )
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