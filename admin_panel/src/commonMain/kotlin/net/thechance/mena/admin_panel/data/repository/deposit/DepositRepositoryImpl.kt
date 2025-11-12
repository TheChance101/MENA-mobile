package net.thechance.mena.admin_panel.data.repository.deposit

import net.thechance.mena.admin_panel.data.remote.api_service.DepositApiService
import net.thechance.mena.admin_panel.data.remote.dto.deposit.DepositRequestDto
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.domain.repository.deposit.DepositRepository
import org.koin.core.annotation.Single

@Single
class DepositRepositoryImpl(
    private val depositApiService: DepositApiService
) : DepositRepository {
    override suspend fun deposit(phoneNumber :String , amount :Double) {
        executeApiSafely<Unit>{
            depositApiService.deposit(DepositRequestDto(phoneNumber , amount))
        }
    }
}