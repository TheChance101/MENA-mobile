package net.thechance.mena.dukan.presentation.viewModel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.dukan.presentation.navigation.DukanNavigator
import net.thechance.mena.dukan.presentation.navigation.DukanRoute

abstract class BaseViewModel<S>(
    initialState: S,
    protected val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val dukanNavigator: DukanNavigator
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

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
                .collectLatest { result ->
                    onCollect(result)
                }
        }
    }

    private fun createExceptionHandler(onError: (Throwable) -> Unit) =
        CoroutineExceptionHandler { _, throwable ->
            onError(throwable)
        }


    protected fun navigate(
        route: DukanRoute,
        navOptions: NavOptions? = null
    ) {
        viewModelScope.launch {
            dukanNavigator.navigate(
                route = route,
                navOptions = navOptions
            )
        }
    }

    protected fun navigateUp() {
        viewModelScope.launch { dukanNavigator.navigateUp() }
    }
}