package net.thechance.mena.wallet.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import net.thechance.mena.wallet.data.database.Statement
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
interface StatementDao {
    @Insert
    suspend fun insertStatement(statement: Statement)

    @Query("SELECT * FROM statement ORDER BY endDate DESC, startDate DESC")
    suspend fun getAllStatement(): List<Statement>

    @Delete
    suspend fun deleteStatement(statement: Statement)

    @Query("SELECT * FROM statement WHERE id = :id")
    suspend fun getStatementById(id: Long): Statement
}