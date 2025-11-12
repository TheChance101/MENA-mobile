package net.thechance.mena.admin_panel.data.mapper.deposit

import net.thechance.mena.admin_panel.data.remote.dto.deposit.DepositRequestDto
import net.thechance.mena.admin_panel.domain.model.DepositQueryParams

fun DepositQueryParams.toRequest(): DepositRequestDto {
    return DepositRequestDto(
        phoneNumber = phoneNumber,
        amount = amount
    )
}