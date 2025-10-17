package net.thechance.mena.trends.presentation.screen.video_description.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.trends.presentation.navigation.Route
import org.koin.core.annotation.Factory

@Factory (binds = [VideoDescriptionArgs::class])
class VideoDescriptionArgsImp(
    saveSateHandle : SavedStateHandle
) : VideoDescriptionArgs {
    override val trendId: String = saveSateHandle.toRoute<Route.VideoDescription>().trendId
}