package net.thechance.mena.core_chat.presentation.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import net.thechance.mena.core_chat.domain.exception.ContactsPermissionDeniedException
import net.thechance.mena.core_chat.domain.exception.NetworkException
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ContactSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params), KoinComponent {

    private val contactsRepository: ContactsRepository by inject()

    override suspend fun doWork(): Result = runCatching {
        contactsRepository.syncContacts()
        Result.success()
    }.getOrElse {
        it.printStackTrace()
        val exception = when (it) {
            is ContactsPermissionDeniedException -> ContactSyncException.PERMISSION_DENIED_EXCEPTION
            is NetworkException -> ContactSyncException.NETWORK_EXCEPTION
            else -> ContactSyncException.UNKNOWN_EXCEPTION
        }
        Result.failure(
            workDataOf(
                ERROR_CODE to exception.code,
                ERROR_MESSAGE to (it.message ?: "Unknown error"),
            )
        )
    }

    companion object {
        const val ERROR_CODE = "errorCode"
        const val ERROR_MESSAGE = "errorMessage"
    }
}