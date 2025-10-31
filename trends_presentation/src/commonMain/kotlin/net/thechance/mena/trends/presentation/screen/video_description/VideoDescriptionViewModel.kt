package net.thechance.mena.trends.presentation.screen.video_description

import net.thechance.mena.trends.presentation.screen.video_description.args.VideoDescriptionArgs
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
internal class VideoDescriptionViewModel(
    @Provided private val videoDescriptionArgs: VideoDescriptionArgs
) : BaseViewModel<VideoDescriptionScreenState, VideoDescriptionEffect>(initialState = VideoDescriptionScreenState()),
    VideoDescriptionInteractionListener {


    override fun onClickBack() {
        sendEffect(VideoDescriptionEffect.NavigateBack)
    }

    override fun onClickNext() {
        sendEffect(
            VideoDescriptionEffect.NavigateToSelectCategories(
                state.value.description.trim(),
                videoDescriptionArgs.trendId
            )
        )
    }

    override fun onDescriptionChanged(newValue: String) {
        updateState {
            copy(
                description = newValue,
                currentNumberOfCharacters = newValue.length,
                isButtonEnabled = newValue.length <= maxNumberOfCharacters
            )
        }
    }
}