package net.thechance.mena.dukan.data.repository

import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.DukanRepository

class DukanRepositoryImpl(
    // TODO inject data source here
) : DukanRepository {

    override suspend fun addDukan(dukan: Dukan) {
        TODO("Not yet implemented")
    }

    override suspend fun getDukanStyles(): List<Dukan.Style> {
        TODO("Not yet implemented")
    }

    override suspend fun getCategories(): List<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun getDukanColors(): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyDukan(): Dukan {
        TODO("Not yet implemented")
    }

    override suspend fun isDukanNameTaken(name: String) {
        TODO("Not yet implemented")
    }

    override suspend fun isUserHasDukan(): Boolean {
        TODO("Not yet implemented")
    }
}