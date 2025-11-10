package net.thechance.mena.trends.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserEngagementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEngagement(engagement: UserEngagement): Long

    /**
     * Gets all UserEngagements for a specific user where watchEndTimestamp is before today at midnight
     * @param userId The user's ID
     * @param midnightTimestamp The timestamp for today at 00:00 (midnight) in milliseconds
     * @return List of UserEngagements before today's midnight
     */
    @Query(
        """
        SELECT * FROM engagements 
        WHERE userId = :userId 
        AND watchEndTimestamp < :midnightTimestamp
        ORDER BY watchEndTimestamp DESC
    """
    )
    suspend fun getEngagementsBeforeTodayOnce(
        userId: String,
        midnightTimestamp: Long
    ): List<UserEngagement>

    /**
     * Deletes all UserEngagements for a specific user where watchEndTimestamp is before today at midnight
     * @param userId The user's ID
     * @param midnightTimestamp The timestamp for today at 00:00 (midnight) in milliseconds
     * @return Number of engagements deleted
     */
    @Query(
        """
        DELETE FROM engagements 
        WHERE userId = :userId 
        AND watchEndTimestamp < :midnightTimestamp
    """
    )
    suspend fun deleteEngagementsBeforeToday(
        userId: String,
        midnightTimestamp: Long
    ): Int

}