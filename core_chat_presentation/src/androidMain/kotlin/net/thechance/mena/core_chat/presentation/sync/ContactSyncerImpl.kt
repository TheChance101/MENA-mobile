package net.thechance.mena.core_chat.presentation.sync

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.core_chat.domain.exception.ContactsPermissionDeniedException
import net.thechance.mena.core_chat.domain.exception.NetworkException
import net.thechance.mena.core_chat.domain.exception.UnknownException
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactSyncer
import java.util.UUID

class ContactSyncerImpl(private val context: Context) : ContactSyncer {

    override suspend fun sync() {
        val request = OneTimeWorkRequestBuilder<ContactSyncWorker>().build()
        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniqueWork("ContactSync", ExistingWorkPolicy.REPLACE, request)
        val info = workManager.awaitWorkInfo(request.id)
        info.throwIfFailed()
    }

    suspend fun WorkManager.awaitWorkInfo(id: UUID): WorkInfo {
        return suspendCancellableCoroutine { cont ->
            val liveData = getWorkInfoByIdLiveData(id)
            val mainHandler = Handler(Looper.getMainLooper())

            val observer = object : Observer<WorkInfo?> {
                override fun onChanged(value: WorkInfo?) {
                    if (value != null && value.state.isFinished) {
                        cont.resume(value) { _, _, _ -> }
                        mainHandler.post {
                            liveData.removeObserver(this)
                        }
                    }
                }
            }

            mainHandler.post { liveData.observeForever(observer) }

            cont.invokeOnCancellation {
                mainHandler.post { liveData.removeObserver(observer) }
            }
        }
    }

    private fun WorkInfo.throwIfFailed() {
        if (state == WorkInfo.State.FAILED) {
            val errorCode = outputData.getInt(ContactSyncWorker.ERROR_CODE, -1)
            val errorMessage = outputData.getString(ContactSyncWorker.ERROR_MESSAGE)
            val exception = ContactSyncException.fromCode(errorCode)

            throw when (exception) {
                ContactSyncException.PERMISSION_DENIED_EXCEPTION ->
                    ContactsPermissionDeniedException("Permission denied to access contacts")
                ContactSyncException.NETWORK_EXCEPTION -> NetworkException(errorMessage)
                ContactSyncException.UNKNOWN_EXCEPTION -> UnknownException(errorMessage)
            }
        }
    }
}
