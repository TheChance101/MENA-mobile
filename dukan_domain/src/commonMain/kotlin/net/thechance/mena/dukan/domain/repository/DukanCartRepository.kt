package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams

interface DukanCartRepository {

    suspend fun updateProductQuantity(params: UpdateProductCartQuantityParams)

}