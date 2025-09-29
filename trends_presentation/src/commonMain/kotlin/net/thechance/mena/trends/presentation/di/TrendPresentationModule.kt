package net.thechance.mena.trends.presentation.di

import net.thechance.mena.trends.presentation.shared.util.video_util.VideoDurationExtractor
import net.thechance.mena.trends.presentation.shared.util.video_util.getVideoDurationExtractor
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("net.thechance.mena.trends.presentation")
class TrendPresentationModule {

    @Single
    fun providesVideoDurationExtractor(): VideoDurationExtractor {
        return getVideoDurationExtractor()
    }
}