package net.thechance.mena.trends.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.data.local.database.TrendsDatabaseConstants.ENGAGEMENTS_TABLE

@Dao
interface UserEngagementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEngagement(engagement: UserEngagement): Long

    @Query(
        """
        SELECT * FROM $ENGAGEMENTS_TABLE 
        WHERE userId = :userId 
        AND watchEndTime < :time
        ORDER BY watchEndTime DESC
    """
    )
    suspend fun getUserEngagementsBeforeGivenTime(
        userId: String,
        time: LocalDateTime
    ): List<UserEngagement>

    @Query(
        """
        DELETE FROM $ENGAGEMENTS_TABLE 
        WHERE userId = :userId 
        AND watchEndTime < :time
    """
    )
    suspend fun deleteUserEngagementsBeforeGivenTime(
        userId: String,
        time: LocalDateTime
    ): Int

}