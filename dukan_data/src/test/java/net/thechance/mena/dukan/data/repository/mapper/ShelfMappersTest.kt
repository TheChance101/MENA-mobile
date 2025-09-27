package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.domain.entity.Shelf
import org.junit.Test
import kotlin.test.assertEquals

class ShelfMappersTest {

    @Test
    fun `Shelf to CreateShelfRequest maps correctly`() {
        val shelf = Shelf(
            id = "123",
            name = "My Shelf",
            dukanId = "456"
        )

        val request = shelf.toCreateShelfRequest()

        assertEquals("My Shelf", request.title)
    }
}
