package net.thechance.mena.trends.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.data.remote.mapper.toUserEngagement
import net.thechance.mena.trends.domain.model.ReelWatchSession
import kotlin.test.Test

internal class ReelWatchSessionMapperTest {

    @Test
    fun `ReelWatchSession should mapped to UserEngagement correctly`() {
        val userEngagement = reelWatchSession.toUserEngagement("1")

        assertThat(userEngagement.userId).isEqualTo("1")
        assertThat(userEngagement.trendId).isEqualTo("2")
        assertThat(userEngagement.watchStartTime).isEqualTo(reelWatchSession.watchStartTime)
        assertThat(userEngagement.watchEndTime).isEqualTo(reelWatchSession.watchEndTime)
        assertThat(userEngagement.videoDurationInMilliseconds).isEqualTo(reelWatchSession.videoDurationInMilliseconds)
        assertThat(userEngagement.percentageOfVideoWatched).isEqualTo(reelWatchSession.percentageOfVideoWatched)
    }

    private companion object {
        val reelWatchSession = ReelWatchSession(
            reelId = "2",
            watchStartTime = LocalDateTime(2024, 12, 10, 15, 30),
            watchEndTime = LocalDateTime(2024, 12, 10, 15, 31),
            videoDurationInMilliseconds = 60000,
            percentageOfVideoWatched = 100.0f
        )
    }
}