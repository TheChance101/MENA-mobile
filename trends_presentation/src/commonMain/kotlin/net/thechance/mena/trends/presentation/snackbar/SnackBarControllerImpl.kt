package net.thechance.mena.trends.presentation.snackbar

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class SnackBarState(
    val data: SnackBarData? = null,
    val isVisible: Boolean = false
)

class SnackBarControllerImpl(
    private val coroutineScope: CoroutineScope,
    private val animationConfig: SnackBarAnimationConfig = SnackBarAnimationConfig()
) : SnackBarController {
    private val _state = MutableStateFlow(SnackBarState())
    val state = _state.asStateFlow()

    private var currentJob: Job? = null
    private val messageQueue = ArrayDeque<SnackBarData>()
    private val queueMutex = Mutex()

    override fun showSnackBar(snackBarData: SnackBarData) {
        coroutineScope.launch {
            queueMutex.withLock { messageQueue.add(snackBarData) }
            if (currentJob?.isActive != true) {
                processNextMessage()
            }
        }
    }

    private fun processNextMessage() {
        currentJob = coroutineScope.launch {
            val nextMessage = getNextMessageFromQueue() ?: return@launch
            displaySnackBar(nextMessage)
            hideSnackBar()
            completeAndProceed(nextMessage)
        }
    }

    private suspend fun getNextMessageFromQueue(): SnackBarData? {
        return queueMutex.withLock { messageQueue.removeFirstOrNull() }
    }

    private suspend fun displaySnackBar(data: SnackBarData) {
        _state.value = SnackBarState(data = data, isVisible = true)
        delay(data.snackbarDuration.timeInMillis)
    }

    private suspend fun hideSnackBar() {
        _state.value = _state.value.copy(isVisible = false)
        delay(animationConfig.durationMillis.toLong())
        resetSnackBarState()
    }

    private fun resetSnackBarState() {
        _state.value = SnackBarState()
    }

    private fun completeAndProceed(data: SnackBarData) {
        data.onComplete?.invoke()
        processNextMessage()
    }
}
