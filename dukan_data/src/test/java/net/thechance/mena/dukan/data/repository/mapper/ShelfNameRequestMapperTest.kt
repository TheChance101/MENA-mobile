package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.mapper.toRequest
import net.thechance.mena.dukan.domain.model.UpdateShelfName
import kotlin.test.Test
import kotlin.test.assertEquals

class ShelfNameRequestMapperTest {
    @Test
    fun `toRequest maps domain model to request correctly`() {
        val domainModel = UpdateShelfName(title = "New Shelf Name")
        val request = domainModel.toRequest()

        assertEquals("New Shelf Name", request.title)
    }
}