package net.thechance.mena.core_chat.data.contacts

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.core_chat.data.contacts.source.remote.ContactSyncer
import java.util.UUID

class ContactSyncerImpl(private val context: Context) : ContactSyncer {

    override suspend fun sync() {
        val request = OneTimeWorkRequestBuilder<ContactSyncWorker>().build()
        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniqueWork("ContactSync", ExistingWorkPolicy.REPLACE, request)
        val info = workManager.awaitWorkInfo(request.id)
        if (info.state == WorkInfo.State.FAILED) throw Exception("Sync failed")
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
}
