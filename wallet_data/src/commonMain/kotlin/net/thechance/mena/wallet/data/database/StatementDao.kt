package net.thechance.mena.wallet.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StatementDao {
    @Insert
    suspend fun insertStatement(statement: Statement)

    @Query("SELECT * FROM statement ORDER BY  createdAt DESC  LIMIT :limit OFFSET :offset")
    suspend fun getAllStatement(limit:Int,offset:Int): List<Statement>

    @Delete
    suspend fun deleteStatement(statement: Statement)

    @Query("SELECT * FROM statement WHERE id = :id")
    suspend fun getStatementById(id: Long): Statement
}