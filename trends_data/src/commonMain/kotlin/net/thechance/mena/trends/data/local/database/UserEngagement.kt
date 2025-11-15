package net.thechance.mena.trends.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import net.thechance.mena.trends.data.local.database.TrendsDatabaseConstants.ENGAGEMENTS_TABLE

@Entity(tableName = ENGAGEMENTS_TABLE)
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
