package net.thechance.mena.trends.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.data.remote.mapper.toUserEngagement
import net.thechance.mena.trends.domain.model.TrendWatchSession
import kotlin.test.Test

internal class TrendWatchSessionMapperTest {

    @Test
    fun `ReelWatchSession should mapped to UserEngagement correctly`() {
        val userEngagement = trendWatchSession.toUserEngagement("1")

        assertThat(userEngagement.userId).isEqualTo("1")
        assertThat(userEngagement.trendId).isEqualTo("2")
        assertThat(userEngagement.watchStartTime).isEqualTo(trendWatchSession.watchStartTime)
        assertThat(userEngagement.watchEndTime).isEqualTo(trendWatchSession.watchEndTime)
        assertThat(userEngagement.videoDurationInMilliseconds).isEqualTo(trendWatchSession.videoDurationInMilliseconds)
        assertThat(userEngagement.percentageOfVideoWatched).isEqualTo(trendWatchSession.percentageOfVideoWatched)
    }

    private companion object {
        val trendWatchSession = TrendWatchSession(
            trendId = "2",
            watchStartTime = LocalDateTime(2024, 12, 10, 15, 30),
            watchEndTime = LocalDateTime(2024, 12, 10, 15, 31),
            videoDurationInMilliseconds = 60000,
            percentageOfVideoWatched = 100.0f
        )
    }
}