package net.thechance.mena.identity.data.dataSource.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import net.thechance.mena.identity.data.dataSource.local.database.model.AddressEntity

@Dao
interface AddressDao {
    @Upsert
    suspend fun upsert(item: AddressEntity)

    @Query("SELECT * FROM Address WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveAddress(): AddressEntity?

    @Query("DELETE FROM Address WHERE isActive = 1")
    suspend fun deleteActiveAddress()

    @Query("DELETE FROM Address")
    suspend fun clearAddress()
}