package net.thechance.mena.trends.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "engagements")
data class UserEngagement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val trendId: String,
    val watchStartTimestamp: Long,
    val watchEndTimestamp: Long,
    val videoDurationInMilliseconds: Long,
    val percentageOfVideoWatched: Float,
)
