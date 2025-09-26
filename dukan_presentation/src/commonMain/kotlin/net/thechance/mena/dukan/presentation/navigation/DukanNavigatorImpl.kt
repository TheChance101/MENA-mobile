package net.thechance.mena.dukan.presentation.navigation

import androidx.navigation.NavOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class DukanNavigatorImpl(
    override val startDestination: DukanRoute
) : DukanNavigator {

    private val _dukanEffects = MutableSharedFlow<DukanEffect>()
    override val dukanEffects = _dukanEffects.asSharedFlow()
    private val mutex = Mutex()
    private var lastNavigateTime = 0L

    override suspend fun navigate(
        route: DukanRoute,
        navOptions: NavOptions?,
    ) = safeNavigation {
        _dukanEffects.emit(
            value = DukanEffect.Navigate(
                route = route,
                navOptions = navOptions
            )
        )
    }


    override suspend fun navigateUp() {
        _dukanEffects.emit(DukanEffect.NavigateUp)
    }

    override suspend fun popBackStackWithArgs(vararg arguments: Pair<String, Any>) {
        _dukanEffects.emit(DukanEffect.PopBackStackWithArgs(arguments = arguments.toMap()))
    }

    private suspend fun safeNavigation(block: suspend () -> Unit) {
        mutex.withLock {
            val now = Clock.System.now().toEpochMilliseconds()
            if (now - lastNavigateTime >= NAVIGATION_DEBOUNCE_INTERVAL_MS) {
                lastNavigateTime = now
                block.invoke()
            }
        }
    }

    companion object {
        private const val NAVIGATION_DEBOUNCE_INTERVAL_MS = 500L
    }
}