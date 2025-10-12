package net.thechance.mena.wallet.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StatementDao {
    @Insert
    suspend fun insertStatement(localStatement: LocalStatement)

    @Query("SELECT * FROM statement ORDER BY  createdAt DESC  LIMIT :limit OFFSET :offset")
    suspend fun getAllStatement(limit:Int,offset:Int): List<LocalStatement>

    @Query("DELETE FROM statement WHERE id = :id")
    suspend fun deleteStatementById(id: Long)

    @Query("SELECT * FROM statement WHERE id = :id")
    suspend fun getStatementById(id: Long): LocalStatement
}