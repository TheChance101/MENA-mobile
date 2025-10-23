package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Dukan

interface DukanDiscoveryRepository {
    suspend fun getEditorPicksDukans(page: Int, size: Int): List<Dukan>
    suspend fun getBestAroundDukans(page: Int, size: Int): List<Dukan>
    suspend fun getDukansByCategory(
        categoryId: String,
        page: Int,
        size: Int
    ): List<Dukan>
}