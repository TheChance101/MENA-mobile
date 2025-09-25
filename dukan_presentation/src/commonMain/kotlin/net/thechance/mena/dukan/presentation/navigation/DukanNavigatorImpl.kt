package net.thechance.mena.dukan.presentation.navigation

import androidx.navigation.NavOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class DukanNavigatorImpl(
    override val startDestination: DukanRoute
) : DukanNavigator {

    private val _dukanEffects = MutableSharedFlow<DukanEffect>()
    override val dukanEffects = _dukanEffects.asSharedFlow()
    private val mutex = Mutex()
    private var lastNavigateTime = 0L

    @OptIn(ExperimentalTime::class)
    override suspend fun navigate(
        route: DukanRoute,
        navOptions: NavOptions?,
    ) {
        mutex.withLock {
            val now = Clock.System.now().toEpochMilliseconds()
            if (now - lastNavigateTime >= NAVIGATION_DEBOUNCE_INTERVAL_MS) {
                lastNavigateTime = now
                _dukanEffects.emit(
                    value = DukanEffect.Navigate(
                        destination = route,
                        navOptions = navOptions
                    )
                )
            }
        }
    }

    override suspend fun navigateUp() {
        _dukanEffects.emit(DukanEffect.NavigateUp)
    }

    companion object {
        private const val NAVIGATION_DEBOUNCE_INTERVAL_MS = 500L
    }
}