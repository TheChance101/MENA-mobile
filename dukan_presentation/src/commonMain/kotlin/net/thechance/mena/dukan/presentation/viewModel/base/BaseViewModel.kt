package net.thechance.mena.dukan.presentation.viewModel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.dukan.presentation.util.pagination.base.BasePagationSource
import net.thechance.mena.dukan.presentation.util.pagination.base.BasePagationSource.Companion.PAGE_SIZE

abstract class BaseViewModel<S, E>(
    initialState: S,
    protected val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _state = MutableStateFlow<S>(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<E>()
    val effect: SharedFlow<E> = _effect.asSharedFlow()

    private var debounceJob: Job? = null


    internal fun updateState(updater: S.() -> S) {
        _state.update(updater)
    }

    protected fun <S> tryToExecute(
        onStart: () -> Unit = {},
        block: suspend () -> S,
        onSuccess: (S) -> Unit = {},
        onError: (exception: Throwable) -> Unit = {},
        dispatcher: CoroutineDispatcher = defaultDispatcher,
    ) {
        onStart()
        val handler = createExceptionHandler(onError)
        viewModelScope.launch(dispatcher + handler) {
            try {
                val result = block()
                onSuccess(result)
            } catch (_: CancellationException) {

            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    protected fun <S> tryToExecuteWithDebounce(
        debounceTime: Long = 200,
        onStart: () -> Unit = {},
        block: suspend () -> S,
        onSuccess: (S) -> Unit = {},
        onError: (exception: Throwable) -> Unit = {},
        dispatcher: CoroutineDispatcher = defaultDispatcher,
    ) {
        onStart()
        debounceJob?.cancel()

        val handler = createExceptionHandler(onError)
        debounceJob = viewModelScope.launch(dispatcher + handler) {
            delay(debounceTime)
            try {
                val result = block()
                onSuccess(result)
            } catch (e: CancellationException){
                return@launch
            }
            catch (e: Exception) {
                onError(e)
            }
        }
    }

    protected fun emitEffect(effect: E) {
        viewModelScope.launch(
            context = defaultDispatcher,
        ) {
            _effect.emit(effect)
        }
    }

    protected fun <S> tryToCollect(
        onStart: () -> Unit = {},
        block: suspend () -> Flow<S>,
        onCollect: suspend (S) -> Unit,
        onError: (exception: Throwable) -> Unit = {},
        dispatcher: CoroutineDispatcher = defaultDispatcher,
    ) {
        onStart()
        val handler = createExceptionHandler(onError)
        viewModelScope.launch(dispatcher + handler) {
            try {
                block()
                    .catch { onError(it) }
                    .collectLatest { result ->
                        onCollect(result)
                    }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    private fun createExceptionHandler(onError: (Throwable) -> Unit) =
        CoroutineExceptionHandler { _, throwable ->
            onError(throwable)
        }

    protected fun <T : Any, R : Any> createPagingSourceFlow(
        onError: (Exception) -> Unit = {},
        mapper: (T) -> R,
        enablePlaceholders: Boolean = false,
        block: suspend (pageNumber: Int, pageSize: Int) -> List<T>
    ): Flow<PagingData<R>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                enablePlaceholders = enablePlaceholders
            ),
            pagingSourceFactory = {
                BasePagationSource(onError = onError, onFetchPage = block)
            }
        ).flow
            .catch{onError(it as Exception)}
            .map { pagingData ->
            pagingData.map(mapper)
        }.cachedIn(viewModelScope)
    }
}