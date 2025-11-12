package net.thechance.mena.admin_panel.data.repository.deposit

import net.thechance.mena.admin_panel.data.mapper.deposit.toRequest
import net.thechance.mena.admin_panel.data.remote.api_service.DepositApiService
import net.thechance.mena.admin_panel.domain.model.DepositQueryParams
import net.thechance.mena.admin_panel.domain.repository.deposit.DepositRepository

class DepositRepositoryImpl(
    private val depositApiService: DepositApiService
) : DepositRepository {
    override suspend fun deposit(depositQueryParams: DepositQueryParams) {
        depositApiService.deposit(depositQueryParams.toRequest())
    }
}