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
        return listOf(
            Category("1", "Category 1", ""),
            Category("2", "Category 2",""),
            Category("3", "Category 3",""),
            Category("4", "Category 4",""),
            Category("5", "Category 5",""),
        )

    }

    override suspend fun getDukanColors(): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyDukan(): Dukan {
        TODO("Not yet implemented")
    }

    override suspend fun isDukanNameTaken(name: String): Boolean {
        // TODO: Implement real API call when backend is available
        return false
    }

    override suspend fun isUserHasDukan(): Boolean {
        TODO("Not yet implemented")
    }
}