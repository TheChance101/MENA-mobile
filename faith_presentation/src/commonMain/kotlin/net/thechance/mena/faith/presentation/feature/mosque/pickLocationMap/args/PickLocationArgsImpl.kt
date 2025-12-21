package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.args

import androidx.lifecycle.SavedStateHandle

class PickLocationArgsImpl(
    savedStateHandle: SavedStateHandle,
) : PickLocationArgs {
    override val latitude: Double? = savedStateHandle["latitude"]
    override val longitude: Double? = savedStateHandle["longitude"]
}