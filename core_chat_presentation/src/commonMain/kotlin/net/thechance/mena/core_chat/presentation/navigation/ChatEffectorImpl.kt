package net.thechance.mena.core_chat.presentation.navigation

import androidx.navigation.NavOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ChatEffectorImpl() : ChatEffector {
    private val _chatEffect = MutableSharedFlow<ChatEffect>()
    override val chatEffect = _chatEffect.asSharedFlow()
    private val mutex = Mutex()
    private var lastNavigateTime = 0L

    override val popBackStackArgsFlow: Flow<Map<String, Any>> =
        chatEffect.filterIsInstance<ChatEffect.PopBackStack>()
            .map { it.arguments }

    @OptIn(ExperimentalTime::class)
    override suspend fun navigate(route: ChatRoute, navOptions: NavOptions?, forceNavigate: Boolean) {
        mutex.withLock {
            val now = Clock.System.now().toEpochMilliseconds()
            if (forceNavigate || now - lastNavigateTime >= EFFECT_DEBOUNCE_MS) {
                lastNavigateTime = now
                _chatEffect.emit(
                    ChatEffect.Navigate(route = route, navOptions = navOptions)
                )
            }
        }
    }

    override suspend fun popBackStack(vararg arguments: Pair<String, Any>) {
        _chatEffect.emit(ChatEffect.PopBackStack(arguments.toMap()))
    }

    override suspend fun setNavigationArgs(vararg arguments: Pair<String, Any>) {
        _chatEffect.emit(ChatEffect.SetNavigationArgs(arguments.toMap()))
    }

    override suspend fun showSnackBar(snackBarData: SnackBarData) {
        _chatEffect.emit(ChatEffect.ShowSnackBar(snackBarData))
    }

    companion object {
        private const val EFFECT_DEBOUNCE_MS = 500L
    }
}