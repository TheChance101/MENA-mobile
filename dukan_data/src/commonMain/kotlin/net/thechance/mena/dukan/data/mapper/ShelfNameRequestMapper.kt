package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.shelf.UpdateShelfNameRequest
import net.thechance.mena.dukan.domain.model.UpdateShelfName

fun UpdateShelfName.toRequest(): UpdateShelfNameRequest {
    return UpdateShelfNameRequest(title = title)
}