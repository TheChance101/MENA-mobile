package net.thechance.mena.admin_panel.domain.repository.deposit

import net.thechance.mena.admin_panel.domain.model.DepositQueryParams

interface DepositRepository {
    suspend fun deposit(depositQueryParams: DepositQueryParams)
}