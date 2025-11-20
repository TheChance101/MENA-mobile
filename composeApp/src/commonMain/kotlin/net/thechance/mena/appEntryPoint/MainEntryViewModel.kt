package net.thechance.mena.appEntryPoint

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainEntryViewModel : ViewModel(), MainEntryInteractionListener {
    private val _state = MutableStateFlow(MainEntryState())
    val state = _state.asStateFlow()

    override fun onDeepLinkChange(deepLink: DeepLink) {
        _state.update { it.copy(deepLink = deepLink) }
    }

    override fun clearDeepLink() {
        _state.update { it.copy(deepLink = null) }
    }

    override fun onBottomNavigationChanged(isShowed: Boolean) {
        _state.update { it.copy(showBottomNavigation = isShowed) }
    }

    override fun setActiveFeature(feature: Feature) {
        _state.update { it.copy(activeFeature = feature) }
    }
}