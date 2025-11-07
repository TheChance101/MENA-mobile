package net.thechance.mena.appEntryPoint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.service.AuthorizationService

class MainEntryViewModel(
    private val authorizationService: AuthorizationService,
) : ViewModel(), MainEntryInteractionListener {
    private val _state = MutableStateFlow(MainEntryState())
    val state = _state.asStateFlow()

    init {
        getUserAccessToken()
    }

    override fun onDeepLinkChange(deepLink: DeepLink) {
        _state.update { it.copy(deepLink = deepLink) }
    }

    override fun clearDeepLink() {
        _state.update { it.copy(deepLink = null) }
    }

    override fun setActiveFeature(feature: Feature) {
        _state.update { it.copy(activeFeature = feature) }
    }

    private fun getUserAccessToken() {
        viewModelScope.launch {
            _state.update { it.copy(userAccessToken = authorizationService.getAccessToken()) }
        }
    }
}