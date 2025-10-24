package net.thechance.mena.trends.presentation.screen.video_description

internal interface VideoDescriptionInteractionListener {
    fun onClickBack()
    fun onClickNext()
    fun onDescriptionChanged(newValue: String)
}