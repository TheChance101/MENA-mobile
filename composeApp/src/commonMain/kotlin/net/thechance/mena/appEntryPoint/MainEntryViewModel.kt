package net.thechance.mena.appEntryPoint

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainEntryViewModel : ViewModel() {
    private val _activeFeature = MutableStateFlow(Feature.CHAT)
    val activeFeature = _activeFeature.asStateFlow()

    fun setActiveFeature(feature: Feature) {
        _activeFeature.value = feature
    }
}