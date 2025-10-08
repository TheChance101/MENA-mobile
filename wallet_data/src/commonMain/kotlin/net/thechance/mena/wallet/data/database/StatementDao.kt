package net.thechance.mena.wallet.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import net.thechance.mena.wallet.data.database.entity.Statement
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
interface StatementDao {
   @Insert
   suspend fun insertStatement(statement : Statement)

   @Query("SELECT * FROM statement")
   suspend fun getAllStatement():List<Statement>

   @OptIn(ExperimentalUuidApi::class)
   @Delete
   suspend fun deleteStatement(statementId : Uuid)



}