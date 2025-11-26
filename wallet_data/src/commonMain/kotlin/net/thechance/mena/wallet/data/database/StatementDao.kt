package net.thechance.mena.wallet.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import net.thechance.mena.wallet.data.dto.local.LocalStatement

@Dao
interface StatementDao {
    @Insert
    suspend fun insertStatement(localStatement: LocalStatement)

    @Query("""
    SELECT * FROM statement 
    WHERE userId = :userId
    ORDER BY createdAt DESC 
    LIMIT :limit OFFSET :offset
    """)
    suspend fun getAllStatement(userId: String, limit: Int, offset: Int): List<LocalStatement>

    @Query("DELETE FROM statement WHERE id = :id")
    suspend fun deleteStatementById(id: String)

    @Query("SELECT * FROM statement WHERE id = :id")
    suspend fun getStatementById(id: String): LocalStatement
}