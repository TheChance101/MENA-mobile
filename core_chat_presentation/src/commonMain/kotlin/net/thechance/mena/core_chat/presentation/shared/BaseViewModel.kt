package net.thechance.mena.core_chat.presentation.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.navigation.ChatRoute
import org.koin.core.component.KoinComponent

open class BaseViewModel<S>(
    initialState: S,
    private val effector: ChatEffector,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    protected val popBackStackArgsFlow = effector.popBackStackArgsFlow

    protected fun navigate(
        route: ChatRoute,
        navOptions: NavOptions? = null,
        forceNavigate: Boolean = false
    ) =
        viewModelScope.launch {
            effector.navigate(route = route, navOptions = navOptions, forceNavigate = forceNavigate)
        }

    protected fun setNavigationArgs(vararg arguments: Pair<String, Any>) =
        viewModelScope.launch { effector.setNavigationArgs(*arguments) }

    protected fun popBackStack(vararg arguments: Pair<String, Any>) =
        viewModelScope.launch { effector.popBackStack(*arguments) }

    protected fun showSnackBar(snackBarData: SnackBarData) {
        viewModelScope.launch {
            effector.showSnackBar(snackBarData)
        }
    }

    fun updateState(updater: (S) -> S) = _state.update(updater)

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
        onCollect: suspend (T?) -> Unit,
        onError: (Throwable) -> Unit = {},
        coroutineScope: CoroutineScope = viewModelScope,
        dispatcher: CoroutineDispatcher = defaultDispatcher,
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