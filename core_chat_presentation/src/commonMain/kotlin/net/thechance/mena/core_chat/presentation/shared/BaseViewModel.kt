package net.thechance.mena.core_chat.presentation.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

open class BaseViewModel<S, E>(initialState: S) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _effectChannel = Channel<E>(Channel.BUFFERED)
    val effect: Flow<E> = _effectChannel.receiveAsFlow()

    private var lastEffect: E? = null
    private var lastTime = 0L
    private val effectDebounceMs = 500L

    fun updateState(updater: (S) -> S) = _state.update(updater)

    protected fun <T> tryToExecute(
        onStart: () -> Unit = {},
        execute: suspend () -> T,
        onSuccess: suspend (T) -> Unit = {},
        onError: (Throwable) -> Unit = {},
        coroutineScope: CoroutineScope = viewModelScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable -> onError(throwable) }
        return coroutineScope.launch(exceptionHandler + dispatcher) {
            onStart()
            val result = execute()
            onSuccess(result)
        }
    }

    @OptIn(ExperimentalTime::class)
    protected fun emitEffect(effect: E) {
        val now = Clock.System.now().toEpochMilliseconds()

        val effectType = effect!!::class
        val lastEffectType = lastEffect?.let { it::class }

        if (effectType == lastEffectType && now - lastTime < effectDebounceMs) return

        lastEffect = effect
        lastTime = now

        viewModelScope.launch {
            _effectChannel.send(effect)
        }
    }

    protected fun <T> tryToCollect(
        onStart: () -> Unit = {},
        collect: () -> Flow<T>,
        onCollect: suspend (T?) -> Unit,
        onError: (Throwable) -> Unit = {},
        coroutineScope: CoroutineScope = viewModelScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable -> onError(throwable) }
        return coroutineScope.launch(exceptionHandler + dispatcher) {
            collect()
                .onStart { onStart() }
                .catch { onError(it) }
                .onEmpty { onCollect(null) }
                .collectLatest { onCollect(it) }
        }
    }

    protected fun <T : Any, R : Any> createPagingFlow(
        pagingSourceFactory: () -> PagingSource<Int, T>, mapper: (T) -> R, pageSize: Int = PAGE_SIZE
    ): Flow<PagingData<R>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                prefetchDistance = 4
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map(mapper)
        }.cachedIn(viewModelScope)
    }

    private companion object {
        private const val PAGE_SIZE = 20
    }
}