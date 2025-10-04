package net.thechance.mena.trends.presentation.di

import net.thechance.mena.trends.presentation.shared.util.video_util.VideoUtilities
import net.thechance.mena.trends.presentation.shared.util.video_util.getVideoUtilities
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("net.thechance.mena.trends.presentation")
class TrendPresentationModule {

    @Single
    fun providesVideoDurationExtractor(): VideoUtilities {
        return getVideoUtilities()
    }
}