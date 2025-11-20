package net.thechance.mena.identity.data.dataSource.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import net.thechance.mena.identity.data.dataSource.local.database.model.UserEntity

@Dao
interface UserDao {
    @Upsert
    suspend fun upsert(item: UserEntity)

    @Query("SELECT * FROM User")
    fun getUser(): Flow<UserEntity?>

    @Query("DELETE FROM User")
    suspend fun deleteUser()
}