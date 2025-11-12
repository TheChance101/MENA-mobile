package net.thechance.mena.trends.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.thechance.mena.trends.data.local.database.TrendsDatabaseConstants.ENGAGEMENTS_TABLE

@Dao
interface UserEngagementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEngagement(engagement: UserEngagement): Long

    /**
     * Gets all UserEngagements for a specific user before a specific time
     * @param userId The user's ID
     * @param timestamp The timestamp
     * @return List of UserEngagements before given timestamp
     */
    @Query(
        """
        SELECT * FROM $ENGAGEMENTS_TABLE 
        WHERE userId = :userId 
        AND watchEndTimestamp < :timestamp
        ORDER BY watchEndTimestamp DESC
    """
    )
    suspend fun getUserEngagementsBeforeGivenTime(
        userId: String,
        timestamp: Long
    ): List<UserEngagement>

    /**
     * Deletes all UserEngagements for a specific user before a specific time
     * @param userId The user's ID
     * @param timestamp The timestamp
     * @return Number of engagements deleted
     */
    @Query(
        """
        DELETE FROM $ENGAGEMENTS_TABLE 
        WHERE userId = :userId 
        AND watchEndTimestamp < :timestamp
    """
    )
    suspend fun deleteUserEngagementsBeforeGivenTime(
        userId: String,
        timestamp: Long
    ): Int

}