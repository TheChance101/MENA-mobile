package net.thechance.mena.dukan.presentation.viewModel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.dukan.presentation.util.pagination.BasePagationSourceNew
import net.thechance.mena.dukan.presentation.util.pagination.BasePagationSourceNew.Companion.PAGING_PAGE_SIZE

abstract class BaseViewModel<S, E>(
    initialState: S,
    protected val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _state = MutableStateFlow<S>(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<E>()
    val effect: SharedFlow<E> = _effect.asSharedFlow()

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
            val result = block()
            onSuccess(result)
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
            block()
                .catch { onError(it) }
                .collectLatest { result ->
                    onCollect(result)
                }
        }
    }

    private fun createExceptionHandler(onError: (Throwable) -> Unit) =
        CoroutineExceptionHandler { _, throwable ->
            onError(throwable)
        }

    private fun <T : Any> createPagingSourceFlow(
        onError: (Throwable) -> Unit = {},
        block: suspend (pageNumber: Int, pageSize: Int) -> List<T>
    ): Flow<PagingData<T>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE,
                initialLoadSize = PAGING_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                BasePagationSourceNew(onError = onError, onFetchPage = block)
            }
        ).flow
    }
}