package net.thechance.mena.trends.presentation.shared.util

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> {
    var job: Job = Job().apply { complete() }
    return onCompletion { job.cancel() }.run {
        flow {
            coroutineScope {
                collect { value ->
                    if (!job.isActive) {
                        emit(value)
                        job = launch { delay(windowDuration) }
                    }
                }
            }
        }
    }
}