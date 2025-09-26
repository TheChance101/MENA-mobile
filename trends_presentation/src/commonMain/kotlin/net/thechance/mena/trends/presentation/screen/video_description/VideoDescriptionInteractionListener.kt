package net.thechance.mena.trends.presentation.screen.video_description

internal interface VideoDescriptionInteractionListener {
    fun onBackClick()
    fun onNextClick()
    fun onDescriptionChanged(newValue: String)
}