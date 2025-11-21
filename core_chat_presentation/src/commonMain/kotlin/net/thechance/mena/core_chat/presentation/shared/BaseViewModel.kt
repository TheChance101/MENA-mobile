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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

open class BaseViewModel<S, E>(
    initialState: S,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<E>()
    val effect = _effect.asSharedFlow()

    protected fun updateState(updater: (S) -> S) = _state.update(updater)

    private var lastEffect: E? = null
    private var lastEffectTime: Long = 0L

    @OptIn(ExperimentalTime::class)
    fun emitEffect(effect: E) {
        viewModelScope.launch(defaultDispatcher) {
            val now = Clock.System.now().toEpochMilliseconds()
            if (effect != lastEffect ||  now - lastEffectTime > 500) {
                _effect.emit(effect)
                lastEffect = effect
                lastEffectTime = now
            }
        }
    }

    protected fun <T> tryToExecute(
        onStart: () -> Unit = {},
        execute: suspend () -> T,
        onSuccess: suspend (T) -> Unit = {},
        onError: (Throwable) -> Unit = {},
        coroutineScope: CoroutineScope = viewModelScope,
        dispatcher: CoroutineDispatcher = defaultDispatcher,
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable -> onError(throwable) }
        return coroutineScope.launch(exceptionHandler + dispatcher) {
            onStart()
            val result = execute()
            onSuccess(result)
        }
    }

    protected fun <T> tryToCollect(
        onStart: () -> Unit = {},
        collect: () -> Flow<T>,
        onCollect: suspend (T) -> Unit,
        onError: (Throwable) -> Unit = {},
        coroutineScope: CoroutineScope = viewModelScope,
        dispatcher: CoroutineDispatcher = defaultDispatcher,
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable -> onError(throwable) }
        return coroutineScope.launch(exceptionHandler + dispatcher) {
            collect()
                .onStart { onStart() }
                .catch { onError(it) }
                .collect { onCollect(it) }
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