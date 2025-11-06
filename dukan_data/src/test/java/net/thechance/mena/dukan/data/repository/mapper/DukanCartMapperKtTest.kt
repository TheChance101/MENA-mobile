package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.dto.cart.CartDto
import net.thechance.mena.dukan.data.dto.cart.UpdateProductCartQuantityRequest
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.data.mapper.toDto
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DukanCartMapperKtTest {

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `toUpdateProductRequest maps params correctly to request object`() {

        val dukanId = "10"
        val productId = "1"

        val params = UpdateProductCartQuantityParams(
            dukanId = dukanId,
            productId = productId,
            quantity = 10
        )

        val request: UpdateProductCartQuantityRequest = params.toDto()

        assertEquals(dukanId, request.dukanId)
        assertEquals(productId, request.productId)
        assertEquals(10, request.quantity)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `CartDto toCartDomain maps correctly`() {
        val id = Uuid.random()
        val dto = CartDto(
            id = id,
            totalPrice = 150.0
        )

        val status = dto.toDomain()

        assertEquals(id, status.id)
        assertEquals(150.0, status.totalPrice)
    }
}